package org.ecommerce.userapi.security;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.userapi.entity.Seller;
import org.ecommerce.userapi.entity.Users;
import org.ecommerce.userapi.entity.type.Role;
import org.ecommerce.userapi.exception.UserErrorCode;
import org.ecommerce.userapi.repository.SellerRepository;
import org.ecommerce.userapi.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthDetailsService {

	private final UserRepository userRepository;
	private final SellerRepository sellerRepository;

	public AuthDetails getUserAuth(String email) throws UsernameNotFoundException {
		Set<SimpleGrantedAuthority> authorities = new HashSet<>();
		Optional<Users> user = userRepository.findByEmail(email);
		if (user.isPresent()) {
			authorities.add(new SimpleGrantedAuthority(Role.USER.getCode()));
			return new AuthDetails(user.get().getId(), user.get().getEmail(), authorities);
		}
		throw new CustomException(UserErrorCode.NOT_FOUND_EMAIL);
	}

	public AuthDetails getSellerAuth(String email) throws UsernameNotFoundException {
		Set<SimpleGrantedAuthority>	authorities = new HashSet<>();
		Optional<Seller> seller = sellerRepository.findByEmail(email);
		if (seller.isPresent()) {
			authorities.add(new SimpleGrantedAuthority(Role.SELLER.getCode()));
			return new AuthDetails(seller.get().getId(), seller.get().getEmail(), authorities);
		}
		throw new CustomException(UserErrorCode.NOT_FOUND_EMAIL);
	}
}
