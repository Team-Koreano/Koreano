package org.ecommerce.userapi.service;

import java.util.Set;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.userapi.dto.AccountDto;
import org.ecommerce.userapi.dto.AccountMapper;
import org.ecommerce.userapi.dto.AddressDto;
import org.ecommerce.userapi.dto.AddressMapper;
import org.ecommerce.userapi.dto.UserDto;
import org.ecommerce.userapi.dto.UserMapper;
import org.ecommerce.userapi.entity.Address;
import org.ecommerce.userapi.entity.Users;
import org.ecommerce.userapi.entity.UsersAccount;
import org.ecommerce.userapi.entity.type.Role;
import org.ecommerce.userapi.exception.UserErrorCode;
import org.ecommerce.userapi.repository.AddressRepository;
import org.ecommerce.userapi.repository.UserRepository;
import org.ecommerce.userapi.repository.UsersAccountRepository;
import org.ecommerce.userapi.security.AuthDetails;
import org.ecommerce.userapi.security.JwtUtils;
import org.ecommerce.userapi.utils.RedisUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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

	private final AddressRepository addressRepository;

	private final UsersAccountRepository usersAccountRepository;

	//TODO : 유저에 관한 RUD API 개발
	//TODO : 계좌에 관한 RUD API 개발
	//TODO : 주소에 관한 RUD API 개발

	/**
	 회원가입 요청

	 @param register 사용자의 생성 정보가 들어간 dto 입니다.
	 @return SellerDto
	 */
	public UserDto registerRequest(final UserDto.Request.Register register) {

		checkDuplicatedPhoneNumberOrEmail(register.email(), register.phoneNumber());

		final Users users = Users.ofRegister(register.email(), register.name(),
			passwordEncoder.encode(register.password()),
			register.gender(), register.age(), register.phoneNumber());
		userRepository.save(users);
		return UserMapper.INSTANCE.toDto(users);
	}

	/**
	 로그인 요청
	 <p>
	 로그인 요청을 보낸 유저의 고유한 상태값(역할 + 식별값 + 이메일)을 통해
	 UniqueKey 를 만든 후  key:value(access,refresh)를 Redis에 저장
	 <p>

	 @param login - 사용자의 로그인 정보가 들어간 dto 입니다
	 @return SellerDto
	 */
	public UserDto loginRequest(final UserDto.Request.Login login) {
		final Users users = userRepository.findByEmail(login.email())
			.orElseThrow(() -> new CustomException(UserErrorCode.NOT_FOUND_EMAIL));
		if (!passwordEncoder.matches(login.password(), users.getPassword())) {
			throw new CustomException(UserErrorCode.IS_NOT_MATCHED_PASSWORD);
		}

		final Set<String> authorization = Set.of(Role.USER.getCode());
		final String accessToken = jwtUtils.createTokens(users, authorization);

		return UserMapper.INSTANCE.fromAccessToken(accessToken);

	}

	/**
	 로그아웃 요청
	 <p>
	 로그아웃시 요청을 보낸 유저의 UniqueKey를 통해  Redis 에서 삭제
	 이로 인해 만약 로그아웃시 이전의 발급한 AccessToken은 유효하지않음 재발급의 대상이 됌
	 <p>

	 @param authDetails - 사용자의 정보가 들어간 userDetail 입니다
	 */
	public void logoutRequest(final AuthDetails authDetails) {

		final String[] authoritiesArray = authDetails.getAuthorities().stream()
			.map(GrantedAuthority::getAuthority)
			.toArray(String[]::new);

		final String roll = authoritiesArray[0];

		final String accessTokenKey = jwtUtils.getAccessTokenKey(authDetails.getUserId(), roll);

		redisUtils.deleteData(accessTokenKey);

	}

	/**
	 주소 등록
	 */

	public AddressDto registerAddress(final AuthDetails authDetails, final AddressDto.Request.Register register) {

		final Users users = userRepository.findById(authDetails.getUserId())
			.orElseThrow(() -> new CustomException(UserErrorCode.NOT_FOUND_EMAIL));

		final Address address = Address.ofRegister(users, register.name(), register.postAddress(), register.detail());

		addressRepository.save(address);

		return AddressMapper.INSTANCE.toDto(address);

	}

	/**
	 계좌 등록 요청

	 @param authDetails - 사용자의 정보가 들어간 userDetail 입니다
	 @param register    - 사용자의 계좌 정보가 들어간 dto 입니다.
	 */
	public AccountDto registerAccount(final AuthDetails authDetails, final AccountDto.Request.Register register) {
		final Users users = userRepository.findById(authDetails.getUserId())
			.orElseThrow(() -> new CustomException(UserErrorCode.NOT_FOUND_EMAIL));

		final UsersAccount account = UsersAccount.ofRegister(users, register.number(), register.bankName());

		usersAccountRepository.save(account);

		return AccountMapper.INSTANCE.toDto(account);
	}

	@Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
	public void checkDuplicatedPhoneNumberOrEmail(final String email, final String phoneNumber) {
		if (userRepository.existsByEmailOrPhoneNumber(email, phoneNumber)) {
			throw new CustomException(UserErrorCode.DUPLICATED_EMAIL);
		}
	}
}
