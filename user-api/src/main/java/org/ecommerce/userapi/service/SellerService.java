package org.ecommerce.userapi.service;

import java.util.Set;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.userapi.dto.SellerDto;
import org.ecommerce.userapi.entity.Seller;
import org.ecommerce.userapi.entity.type.Role;
import org.ecommerce.userapi.exception.UserErrorCode;
import org.ecommerce.userapi.repository.SellerRepository;
import org.ecommerce.userapi.security.AuthDetails;
import org.ecommerce.userapi.security.JwtUtils;
import org.ecommerce.userapi.utils.RedisUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class SellerService {

	private final BCryptPasswordEncoder passwordEncoder;

	private final SellerRepository sellerRepository;

	private final JwtUtils jwtUtils;

	private final RedisUtils redisUtils;

	public SellerDto.Response.Register registerRequest(SellerDto.Request.Register createSeller) {

		checkDuplicateEmail(createSeller.email());
		checkDuplicatePhoneNumber(createSeller.phoneNumber());

		Seller seller = Seller.ofRegister(createSeller.email(), createSeller.name(),
			passwordEncoder.encode(createSeller.password()),
			createSeller.address(), createSeller.phoneNumber());
		sellerRepository.save(seller);

		return SellerDto.Response.Register.of(seller);
	}
	public SellerDto.Response.Login loginRequest(SellerDto.Request.Login login) {
		Seller seller = sellerRepository.findByEmail(login.email()).orElseThrow(()-> new CustomException(UserErrorCode.NOT_FOUND_EMAIL));
		if (!passwordEncoder.matches(login.password(),seller.getPassword())){
			throw new CustomException(UserErrorCode.IS_NOT_MATCHED_PASSWORD);
		}

		Set<String> authorization = Set.of(Role.SELLER.getCode());
		String accessToken = jwtUtils.createTokens(seller, authorization);

		return SellerDto.Response.Login.of(accessToken);
	}

	public void logoutRequest(AuthDetails authDetails) {
		String accessTokenKey = jwtUtils.getAccessTokenKey(authDetails.getUserId(),
			authDetails.getAuthorities().toString().replace("[", "").replace("]", ""));
		redisUtils.deleteData(accessTokenKey);
	}

	private void checkDuplicateEmail(String email) {
		if (sellerRepository.existsByEmail(email)) {
			throw new CustomException(UserErrorCode.DUPLICATED_EMAIL);
		}
	}

	private void checkDuplicatePhoneNumber(String phoneNumber) {
		if (sellerRepository.existsByPhoneNumber(phoneNumber)) {
			throw new CustomException(UserErrorCode.DUPLICATED_PHONENUMBER);
		}
	}
}
