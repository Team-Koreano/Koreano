package org.ecommerce.userapi.security;

import java.util.HashSet;
import java.util.Set;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.userapi.entity.Seller;
import org.ecommerce.userapi.entity.Users;
import org.ecommerce.userapi.entity.type.Role;
import org.ecommerce.userapi.exception.UserErrorCode;
import org.ecommerce.userapi.repository.SellerRepository;
import org.ecommerce.userapi.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthDetailsService {

	private final UserRepository userRepository;
	private final SellerRepository sellerRepository;

	public AuthDetails getUserAuth(Integer userId) {
		Set<SimpleGrantedAuthority> authorities = new HashSet<>();
		Users user = userRepository.findById(userId).orElseThrow(()-> new CustomException(UserErrorCode.NOT_FOUND_EMAIL));
			authorities.add(new SimpleGrantedAuthority(Role.USER.getCode()));
			return new AuthDetails(user.getId(), user.getEmail(), authorities);
	}

	public AuthDetails getSellerAuth(Integer sellerId)  {
		Set<SimpleGrantedAuthority>	authorities = new HashSet<>();
		Seller seller = sellerRepository.findById(sellerId).orElseThrow(()-> new CustomException(UserErrorCode.NOT_FOUND_EMAIL));
			authorities.add(new SimpleGrantedAuthority(Role.SELLER.getCode()));
			return new AuthDetails(seller.getId(), seller.getEmail(), authorities);
		}
	}
