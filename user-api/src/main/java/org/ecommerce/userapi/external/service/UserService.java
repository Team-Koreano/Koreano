package org.ecommerce.userapi.external.service;

import java.util.List;
import java.util.Objects;
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
import org.ecommerce.userapi.entity.enumerated.Role;
import org.ecommerce.userapi.exception.UserErrorCode;
import org.ecommerce.userapi.provider.RedisProvider;
import org.ecommerce.userapi.repository.AddressRepository;
import org.ecommerce.userapi.repository.UserRepository;
import org.ecommerce.userapi.repository.UsersAccountRepository;
import org.ecommerce.userapi.security.AuthDetails;
import org.ecommerce.userapi.security.JwtProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class UserService {

	private final UserRepository userRepository;

	private final BCryptPasswordEncoder passwordEncoder;

	private final JwtProvider jwtProvider;

	private final AddressRepository addressRepository;

	private final UsersAccountRepository usersAccountRepository;

	private final RedisProvider redisProvider;
	//TODO : 유저에 관한 RUD API 개발
	//TODO : 계좌에 관한 RUD API 개발
	//TODO : 주소에 관한 RUD API 개발

	/**
	 * 회원가입 요청
	 * <p>
	 * 회원가입 요청에 대한 메서드입니다
	 * <p>
	 * @author Hong
	 *
	 * @param register- 회원 가입 요청에 대한 정보
	 * @return UserDto
	 */
	public UserDto registerRequest(final UserDto.Request.Register register) {

		checkDuplicatedPhoneNumberOrEmail(register.email(), register.phoneNumber());

		final Users users = Users.ofRegister(register.email(), register.name(),
			passwordEncoder.encode(register.password()),
			register.gender(), register.age(), register.phoneNumber());

		userRepository.save(users);

		return UserMapper.INSTANCE.userToDto(users);
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
	public UserDto loginRequest(final UserDto.Request.Login login, HttpServletResponse response) {

		final Users user = userRepository.findUsersByEmail(login.email())
			.filter(users -> passwordEncoder.matches(login.password(), users.getPassword()))
			.orElseThrow(() -> new CustomException(UserErrorCode.NOT_FOUND_EMAIL_OR_NOT_MATCHED_PASSWORD));

		if (!user.isValidUser()) {
			throw new CustomException(UserErrorCode.IS_NOT_VALID_USER);
		}

		final Set<String> authorization = Set.of(Role.USER.getCode());

		return UserMapper.INSTANCE.accessTokenToDto(
			jwtProvider.createUserTokens(user.getId(), authorization, response));
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
		jwtProvider.removeTokens(
			jwtProvider.getAccessTokenKey(authDetails.getId(), authDetails.getRoll()),
			jwtProvider.getRefreshTokenKey(authDetails.getId(), authDetails.getRoll())
		);
	}

	/**
	 * 주소 등록
	 * <p>
	 * 주소 등록에 관한 메서드 입니다.
	 * <p>
	 * @author Hong
	 *
	 * @param authDetails - 사용자 로그인 정보가 담긴 객체
	 * @param register - 회원 주소 등록에 관련된 객체
	 * @return AddressDto
	 */
	public AddressDto createAddress(final AuthDetails authDetails, final AddressDto.Request.Register register) {

		final Users users = userRepository.findById(authDetails.getId())
			.orElseThrow(() -> new CustomException(UserErrorCode.NOT_FOUND_EMAIL));

		final Address address = Address.ofRegister(users, register.name(), register.postAddress(), register.detail());

		addressRepository.save(address);

		return AddressMapper.INSTANCE.addressToDto(address);

	}

	/**
	 계좌 등록 요청
	 @param authDetails - 사용자의 정보가 들어간 userDetail 입니다
	 @param register    - 사용자의 계좌 정보가 들어간 dto 입니다.
	 */
	public AccountDto createAccount(final AuthDetails authDetails, final AccountDto.Request.Register register) {
		final Users users = userRepository.findById(authDetails.getId())
			.orElseThrow(() -> new CustomException(UserErrorCode.NOT_FOUND_EMAIL));

		final UsersAccount account = UsersAccount.ofRegister(users, register.number(), register.bankName());

		usersAccountRepository.save(account);

		return AccountMapper.INSTANCE.userAccountToDto(account);
	}

	/**
	 * 엑세스 토큰을 재발급 하는 로직
	 * @author Hong
	 *
	 * @param bearerToken - 리프래시 토큰
	 * @param response - 쿠키에 담기위해 사용되는 변수
	 * @return - UserDto
	 */
	public UserDto reissueAccessToken(final String bearerToken, HttpServletResponse response) {

		String refreshTokenKey = jwtProvider.getRefreshTokenKey(jwtProvider.getId(bearerToken),
			jwtProvider.getRoll(bearerToken));

		if (!redisProvider.hasKey(refreshTokenKey)) {
			throw new CustomException(UserErrorCode.PLEASE_RELOGIN);
		}

		final String refreshToken = redisProvider.getData(refreshTokenKey);

		return UserMapper.INSTANCE.accessTokenToDto(
			jwtProvider.createUserTokens(jwtProvider.getId(refreshToken),
				Set.of(jwtProvider.getRoll(refreshToken)), response));
	}

	public void withdrawUser(final UserDto.Request.Withdrawal withdrawal, final AuthDetails authDetails) {
		Users user = userRepository.findById(authDetails.getId())
			.orElseThrow(() -> new CustomException(UserErrorCode.NOT_FOUND_EMAIL_OR_NOT_MATCHED_PASSWORD));

		isValidUser(withdrawal, user);

		List<UsersAccount> usersAccounts = usersAccountRepository.findByUsersId(user.getId());
		if (usersAccounts.isEmpty()) {
			throw new CustomException(UserErrorCode.NOT_FOUND_ACCOUNT);
		}
		usersAccounts.forEach(UsersAccount::withdrawal);

		List<Address> addresses = addressRepository.findByUsersId(user.getId());
		if (addresses.isEmpty()) {
			throw new CustomException(UserErrorCode.NOT_FOUND_ADDRESS);
		}
		addresses.forEach(Address::withdrawal);

		user.withdrawal();
	}

	private void checkDuplicatedPhoneNumberOrEmail(final String email, final String phoneNumber) {
		if (userRepository.existsByEmailOrPhoneNumber(email, phoneNumber)) {
			throw new CustomException(UserErrorCode.DUPLICATED_EMAIL_OR_PHONENUMBER);
		}
	}

	private void isValidUser(final UserDto.Request.Withdrawal withdrawal, final Users users) {
		if (
			!passwordEncoder.matches(withdrawal.password(), users.getPassword()) ||
				!Objects.equals(withdrawal.email(), users.getEmail()) ||
				!Objects.equals(withdrawal.phoneNumber(), users.getPhoneNumber())
		) {
			throw new CustomException(UserErrorCode.NOT_FOUND_EMAIL_OR_NOT_MATCHED_PASSWORD);
		}
	}
}
