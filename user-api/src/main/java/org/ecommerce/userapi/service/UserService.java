package org.ecommerce.userapi.service;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.userapi.dto.UserDto;
import org.ecommerce.userapi.entity.Users;
import org.ecommerce.userapi.exception.UserErrorCode;
import org.ecommerce.userapi.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

	private final BCryptPasswordEncoder passwordEncoder;

	@Transactional
	public UserDto.Response.Register registerUser(UserDto.Request.Register createUser) {

		checkDuplicateEmail(createUser.email());
		checkDuplicatePhoneNumber(createUser.phoneNumber());

		Users users = Users.create(createUser.email(), createUser.name(), passwordEncoder.encode(createUser.password()),
			createUser.gender(), createUser.age(), createUser.phoneNumber());
		userRepository.save(users);
		return UserDto.Response.Register.of(users);
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
