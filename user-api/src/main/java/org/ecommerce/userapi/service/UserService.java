package org.ecommerce.userapi.service;

import java.util.Set;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.userapi.dto.UserDto;
import org.ecommerce.userapi.entity.Users;
import org.ecommerce.userapi.entity.type.Role;
import org.ecommerce.userapi.exception.UserErrorCode;
import org.ecommerce.userapi.repository.UserRepository;
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
public class UserService {

	private final UserRepository userRepository;

	private final BCryptPasswordEncoder passwordEncoder;


	private final JwtUtils jwtUtils;

	private final RedisUtils redisUtils;

	//TODO : 다음 이슈 Refactoring 하기
	public UserDto.Response.Register registerRequest(UserDto.Request.Register createUser) {

		checkDuplicateEmail(createUser.email());
		checkDuplicatePhoneNumber(createUser.phoneNumber());

		Users users = Users.ofRegister(createUser.email(), createUser.name(), passwordEncoder.encode(createUser.password()),
			createUser.gender(), createUser.age(), createUser.phoneNumber());
		userRepository.save(users);
		return UserDto.Response.Register.of(users);
	}

	public UserDto.Response.Login loginRequest(UserDto.Request.Login login) {
		Users users = userRepository.findByEmail(login.email()).orElseThrow(()-> new CustomException(UserErrorCode.NOT_FOUND_EMAIL));
		if (!passwordEncoder.matches(login.password(), users.getPassword())){
			throw new CustomException(UserErrorCode.IS_NOT_MATCHED_PASSWORD);
		}

		Set<String> authorization = Set.of(Role.USER.getCode());
		String accessToken = jwtUtils.createTokens(users, authorization);

		return UserDto.Response.Login.of(accessToken);

	}

	public void logoutRequest(AuthDetails authDetails) {
		String accessTokenKey = jwtUtils.getAccessTokenKey(authDetails.getUserId(),
			authDetails.getAuthorities().toString().replace("[","").replace("]",""));
		redisUtils.deleteData(accessTokenKey);
	}
	private void checkDuplicateEmail(String email) {
		if (userRepository.existsByEmail(email)) {
			throw new CustomException(UserErrorCode.DUPLICATED_EMAIL);
		}
	}

	private void checkDuplicatePhoneNumber(String phoneNumber) {
		if (userRepository.existsByPhoneNumber(phoneNumber)) {
			throw new CustomException(UserErrorCode.DUPLICATED_PHONENUMBER);
		}
	}
}
