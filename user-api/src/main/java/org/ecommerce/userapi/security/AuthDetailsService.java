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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthDetailsService implements UserDetailsService {

	private final UserRepository userRepository;
	private final SellerRepository sellerRepository;

	@Override
	public AuthDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Set<SimpleGrantedAuthority> authorities = new HashSet<>();
		// 일반 사용자 정보 가져오기
		Optional<Users> user = userRepository.findByEmail(email);
		if (user.isPresent()) {
			// 권한 저장
			authorities.add(new SimpleGrantedAuthority(Role.USER.getCode()));
			// AuthDetails 의 필요한 정보를 저장
			return new AuthDetails(user.get().getId(),user.get().getEmail(),authorities);
		}
		authorities = new HashSet<>();
		// 판매자 정보 가져오기
		Optional<Seller> seller = sellerRepository.findByEmail(email);
		if (seller.isPresent()) {
			authorities.add(new SimpleGrantedAuthority(Role.SELLER.getCode()));
			return new AuthDetails(seller.get().getId(), seller.get().getEmail(),authorities);
		}
		throw new CustomException(UserErrorCode.NOT_FOUND_EMAIL);
	}
}
