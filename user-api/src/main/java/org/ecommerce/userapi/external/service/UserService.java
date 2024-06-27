package org.ecommerce.userapi.external.service;

import java.util.Set;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.common.provider.JwtProvider;
import org.ecommerce.common.security.AuthDetails;
import org.ecommerce.userapi.client.PaymentServiceClient;
import org.ecommerce.userapi.dto.AccountDto;
import org.ecommerce.userapi.dto.AccountMapper;
import org.ecommerce.userapi.dto.AddressDto;
import org.ecommerce.userapi.dto.AddressMapper;
import org.ecommerce.userapi.dto.UserDto;
import org.ecommerce.userapi.dto.UserMapper;
import org.ecommerce.userapi.dto.request.CreateAccountRequest;
import org.ecommerce.userapi.dto.request.CreateAddressRequest;
import org.ecommerce.userapi.dto.request.CreateUserBeanPayRequest;
import org.ecommerce.userapi.dto.request.CreateUserRequest;
import org.ecommerce.userapi.dto.request.DeleteUserBeanPayRequest;
import org.ecommerce.userapi.dto.request.LoginUserRequest;
import org.ecommerce.userapi.dto.request.WithdrawalUserRequest;
import org.ecommerce.userapi.entity.Address;
import org.ecommerce.userapi.entity.Users;
import org.ecommerce.userapi.entity.UsersAccount;
import org.ecommerce.userapi.entity.enumerated.Role;
import org.ecommerce.userapi.exception.UserErrorCode;
import org.ecommerce.userapi.provider.RedisProvider;
import org.ecommerce.userapi.repository.AddressRepository;
import org.ecommerce.userapi.repository.UserRepository;
import org.ecommerce.userapi.repository.UsersAccountRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.annotations.VisibleForTesting;

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

	private final PaymentServiceClient paymentServiceClient;

	private final UserTokenService userTokenService;

	//TODO : 계좌에 관한 RUD API 개발
	//TODO : 주소에 관한 RUD API 개발

	/**
	 * 회원가입 요청
	 * <p>
	 * 회원가입 요청에 대한 메서드입니다
	 * <p>
	 * @author Hong
	 *
	 * @param createRequest- 회원 가입 요청에 대한 정보
	 * @return UserDto
	 */
	public UserDto registerRequest(final CreateUserRequest createRequest) {

		checkDuplicatedPhoneNumberOrEmail(createRequest.email(), createRequest.phoneNumber());

		Users users = userRepository.save(Users.ofRegister(createRequest.email(), createRequest.name(),
			passwordEncoder.encode(createRequest.password()),
			createRequest.gender(), createRequest.age(), createRequest.phoneNumber()));

		paymentServiceClient.createUserBeanPay(new CreateUserBeanPayRequest(users.getId()));

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
	public UserDto loginRequest(final LoginUserRequest login, HttpServletResponse response) {

		final Users user = userRepository.findUsersByEmailAndIsDeletedIsFalse(login.email());

		if (user == null || !checkIsMatchedPassword(login.password(), user.getPassword()))
			throw new CustomException(UserErrorCode.IS_NOT_MATCHED_EMAIL_OR_PASSWORD);

		if (!user.isValidStatus())
			throw new CustomException(UserErrorCode.IS_NOT_VALID_USER);

		final Set<String> authorization = Set.of(Role.USER.getCode());

		return UserMapper.INSTANCE.toDto(
			userTokenService.createUserTokens(user.getId(), authorization, response));
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
		userTokenService.removeTokens(
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
	public AddressDto createAddress(final AuthDetails authDetails, final CreateAddressRequest register) {
		final Users users = userRepository.findUsersByIdAndIsDeletedIsFalse(authDetails.getId());

		if (users == null)
			throw new CustomException(UserErrorCode.NOT_FOUND_USER);

		final Address address = Address.ofRegister(users, register.name(), register.postAddress(), register.detail());

		addressRepository.save(address);

		return AddressMapper.INSTANCE.toDto(address);

	}

	/**
	 계좌 등록 요청
	 @param authDetails - 사용자의 정보가 들어간 userDetail 입니다
	 @param register    - 사용자의 계좌 정보가 들어간 dto 입니다.
	 */
	public AccountDto createAccount(final AuthDetails authDetails,
		final CreateAccountRequest register) {

		final Users users = userRepository.findUsersByIdAndIsDeletedIsFalse(authDetails.getId());

		if (users == null)
			throw new CustomException(UserErrorCode.NOT_FOUND_USER);

		final UsersAccount account = UsersAccount.ofRegister(users, register.number(), register.bankName());

		usersAccountRepository.save(account);

		return AccountMapper.INSTANCE.toDto(account);
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

		final String refreshTokenKey = jwtProvider.getRefreshTokenKey(jwtProvider.getId(bearerToken),
			jwtProvider.getRoll(bearerToken));

		if (!redisProvider.hasKey(refreshTokenKey))
			throw new CustomException(UserErrorCode.PLEASE_RELOGIN);

		final String refreshToken = redisProvider.getData(refreshTokenKey);

		return UserMapper.INSTANCE.toDto(
			userTokenService.createUserTokens(jwtProvider.getId(refreshToken),
				Set.of(jwtProvider.getRoll(refreshToken)), response));
	}

	/**
	 * 회원 탈퇴 로직입니다
	 * <p>
	 * 탈퇴 요청이 들어오면 해당 유저의 정보를 확인하여 탈퇴를 하도록 하는 로직입니다.
	 * <p>
	 * @author Hong
	 * @param withdrawal - 탈퇴 요청 dto
	 * @return void
	 */
	public void withdrawUser(final WithdrawalUserRequest withdrawal, final AuthDetails authDetails) {
		final Users users = userRepository.findUsersByIdAndIsDeletedIsFalse(authDetails.getId());

		if (users == null || !checkIsMatchedPassword(withdrawal.password(), users.getPassword()))
			throw new CustomException(UserErrorCode.IS_NOT_MATCHED_EMAIL_OR_PASSWORD);

		paymentServiceClient.deleteUserBeanPay(new DeleteUserBeanPayRequest(users.getId()));

		if (!users.isValidUser(withdrawal.email(), withdrawal.phoneNumber())) {
			throw new CustomException(UserErrorCode.IS_NOT_VALID_USER);
		}

		users.withdrawal();

		userTokenService.removeTokens(jwtProvider.getAccessTokenKey(authDetails.getId(), authDetails.getRoll()),
			jwtProvider.getRefreshTokenKey(authDetails.getId(), authDetails.getRoll()));
	}

	private void checkDuplicatedPhoneNumberOrEmail(final String email, final String phoneNumber) {
		if (userRepository.existsByEmailOrPhoneNumber(email, phoneNumber))
			throw new CustomException(UserErrorCode.DUPLICATED_EMAIL_OR_PHONENUMBER);
	}

	@VisibleForTesting
	public boolean checkIsMatchedPassword(String requestPassword, String userPassword) {
		return passwordEncoder.matches(requestPassword, userPassword);
	}
}
