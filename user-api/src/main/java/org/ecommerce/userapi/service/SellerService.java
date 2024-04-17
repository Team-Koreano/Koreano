package org.ecommerce.userapi.service;

import java.util.Set;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.userapi.dto.SellerDto;
import org.ecommerce.userapi.dto.SellerMapper;
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

	/**
	 * 회원가입 요청
	 * @author 홍종민
	 * @return SellerDto
	 */
	public SellerDto registerRequest(SellerDto.Request.Register createSeller) {

		checkDuplicateEmail(createSeller.email());
		checkDuplicatePhoneNumber(createSeller.phoneNumber());

		final Seller seller = Seller.ofRegister(createSeller.email(), createSeller.name(),
			passwordEncoder.encode(createSeller.password()),
			createSeller.address(), createSeller.phoneNumber());
		sellerRepository.save(seller);
		return  SellerMapper.INSTANCE.toDto(seller);
	}

	/**
	 * 로그인 요청
	 *
	 * 로그인 요청을 보낸 유저의 고유한 상태값(역할 + 식별값 + 이메일)을 통해
	 * UniqueKey 를 만든 후  key:value(access,refresh)를 Redis에 저장
	 *
	 * @author 홍종민
	 * @return SellerDto
	 */
	public SellerDto loginRequest(SellerDto.Request.Login login) {
		final Seller seller = sellerRepository.findByEmail(login.email()).orElseThrow(()-> new CustomException(UserErrorCode.NOT_FOUND_EMAIL));

		if (!passwordEncoder.matches(login.password(),seller.getPassword())){
			throw new CustomException(UserErrorCode.IS_NOT_MATCHED_PASSWORD);
		}

		final Set<String> authorization = Set.of(Role.SELLER.getCode());
		final String accessToken = jwtUtils.createTokens(seller, authorization);

		return SellerMapper.INSTANCE.fromAccessToken(accessToken);
	}


	/**
	 * 로그아웃 요청
	 *
	 * 로그아웃시 요청을 보낸 유저의 UniqueKey를 통해  Redis 에서 삭제
	 * 이로 인해 만약 로그아웃시 이전의 발급한 AccessToken은 유효하지않음 재발급의 대상이 됌
	 *
	 * @author 홍종민
	 */
	public void logoutRequest(final AuthDetails authDetails) {
		final String accessTokenKey = jwtUtils.getAccessTokenKey(authDetails.getUserId(),
			authDetails.getAuthorities().toString().replace("[", "").replace("]", ""));
		redisUtils.deleteData(accessTokenKey);
	}

	@Transactional(readOnly = true)
	public void checkDuplicateEmail(final String email) {
		if (sellerRepository.existsByEmail(email)) {
			throw new CustomException(UserErrorCode.DUPLICATED_EMAIL);
		}
	}

	@Transactional(readOnly = true)
	public void checkDuplicatePhoneNumber(final String phoneNumber) {
		if (sellerRepository.existsByPhoneNumber(phoneNumber)) {
			throw new CustomException(UserErrorCode.DUPLICATED_PHONENUMBER);
		}
	}
}
