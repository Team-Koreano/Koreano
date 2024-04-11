package org.ecommerce.userapi.service;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.userapi.dto.SellerDto;
import org.ecommerce.userapi.entity.Seller;
import org.ecommerce.userapi.exception.UserErrorCode;
import org.ecommerce.userapi.repository.SellerRepository;
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

	public SellerDto.Response.Register registerRequest(SellerDto.Request.Register createSeller) {

		checkDuplicateEmail(createSeller.email());
		checkDuplicatePhoneNumber(createSeller.phoneNumber());

		Seller seller = Seller.ofRegister(createSeller.email(), createSeller.name(),
			passwordEncoder.encode(createSeller.password()),
			createSeller.address(), createSeller.phoneNumber());
		sellerRepository.save(seller);

		return SellerDto.Response.Register.of(seller);
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
