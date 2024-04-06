package org.ecommerce.userapi.service;

import org.ecommerce.userapi.dto.UserDto;
import org.ecommerce.userapi.entity.Status;
import org.ecommerce.userapi.entity.Users;
import org.ecommerce.userapi.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

		private final UserRepository userRepository;

		public ResponseEntity<?> createUser(UserDto.Request.CreateUserDto createUser){

			Users user = Users.builder()
				.age(createUser.age())
				.beanPay(0)
				.email(createUser.email())
				.name(createUser.name())
				.isDeleted(false)
				.status(Status.GENERAL)
				.phoneNumber(createUser.phoneNumber())
				.password(createUser.password())
				.gender(createUser.gender())
				.build();

			userRepository.save(user);

			return ResponseEntity.ok(createUser);
		}
}
