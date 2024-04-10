package org.ecommerce.userapi.service;

import org.ecommerce.userapi.dto.SellerDto;
import org.ecommerce.userapi.entity.Seller;
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


	public SellerDto.Response.Register registerUser(SellerDto.Request.Register createSeller) {

		Seller seller = Seller.create(createSeller.email(), createSeller.name(),
			passwordEncoder.encode(createSeller.password()),
			createSeller.address(), createSeller.phoneNumber());
		sellerRepository.save(seller);

		return SellerDto.Response.Register.of(seller);
	}

	private void CheckDuplicateEmail(String email){
		if (sellerRepository.existsByEmail(email)){
		}
	}
}
