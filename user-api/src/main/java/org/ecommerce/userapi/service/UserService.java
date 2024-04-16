package org.ecommerce.userapi.service;

import java.util.Set;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.userapi.dto.UserDto;
import org.ecommerce.userapi.dto.UserMapper;
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


	/**
	 * 회원가입 요청
	 * @author 홍종민
	 * @return SellerDto
	 */
	public UserDto registerRequest(final UserDto.Request.Register register) {

		checkDuplicateEmail(register.email());
		checkDuplicatePhoneNumber(register.phoneNumber());

		final Users users = Users.ofRegister(register.email(), register.name(), passwordEncoder.encode(register.password()),
			register.gender(), register.age(), register.phoneNumber());
		userRepository.save(users);
		return UserMapper.INSTANCE.toDto(users);
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
	public UserDto loginRequest(final UserDto.Request.Login login) {
		final Users users = userRepository.findByEmail(login.email()).orElseThrow(()-> new CustomException(UserErrorCode.NOT_FOUND_EMAIL));
		if (!passwordEncoder.matches(login.password(), users.getPassword())){
			throw new CustomException(UserErrorCode.IS_NOT_MATCHED_PASSWORD);
		}

		final Set<String> authorization = Set.of(Role.USER.getCode());
		final String accessToken = jwtUtils.createTokens(users, authorization);

		return UserMapper.INSTANCE.fromAccessToken(accessToken);

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
			authDetails.getAuthorities().toString().replace("[","").replace("]",""));
		redisUtils.deleteData(accessTokenKey);
	}

	@Transactional(readOnly = true)
	public void checkDuplicateEmail(String email) {
		if (userRepository.existsByEmail(email)) {
			throw new CustomException(UserErrorCode.DUPLICATED_EMAIL);
		}
	}

	@Transactional(readOnly = true)
	public void checkDuplicatePhoneNumber(String phoneNumber) {
		if (userRepository.existsByPhoneNumber(phoneNumber)) {
			throw new CustomException(UserErrorCode.DUPLICATED_PHONENUMBER);
		}
	}
}
