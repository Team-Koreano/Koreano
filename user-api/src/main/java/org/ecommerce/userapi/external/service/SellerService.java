package org.ecommerce.userapi.external.service;

import java.util.Set;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.common.provider.JwtProvider;
import org.ecommerce.common.security.AuthDetails;
import org.ecommerce.userapi.client.PaymentServiceClient;
import org.ecommerce.userapi.dto.AccountDto;
import org.ecommerce.userapi.dto.AccountMapper;
import org.ecommerce.userapi.dto.SellerDto;
import org.ecommerce.userapi.dto.SellerMapper;
import org.ecommerce.userapi.dto.request.CreateAccountRequest;
import org.ecommerce.userapi.dto.request.CreateSellerBeanPayRequest;
import org.ecommerce.userapi.dto.request.CreateSellerRequest;
import org.ecommerce.userapi.dto.request.DeleteSellerBeanPayRequest;
import org.ecommerce.userapi.dto.request.LoginSellerRequest;
import org.ecommerce.userapi.dto.request.WithdrawalSellerRequest;
import org.ecommerce.userapi.entity.Seller;
import org.ecommerce.userapi.entity.SellerAccount;
import org.ecommerce.userapi.entity.enumerated.Role;
import org.ecommerce.userapi.exception.UserErrorCode;
import org.ecommerce.userapi.provider.RedisProvider;
import org.ecommerce.userapi.repository.SellerAccountRepository;
import org.ecommerce.userapi.repository.SellerRepository;
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
public class SellerService {

	private final BCryptPasswordEncoder passwordEncoder;

	private final SellerRepository sellerRepository;

	private final JwtProvider jwtProvider;

	private final RedisProvider redisProvider;

	private final SellerAccountRepository sellerAccountRepository;

	private final PaymentServiceClient paymentServiceClient;

	private final SellerTokenService sellerTokenService;

	//TODO : 계좌에 관한 RUD API 개발

	/**
	 * 회원가입 요청
	 * @author 홍종민
	 * @param requestSeller 사용자의 생성 정보가 들어간 dto 입니다.
	 * @return SellerDto
	 */
	public SellerDto registerRequest(CreateSellerRequest requestSeller) {

		checkDuplicatedPhoneNumberOrEmail(requestSeller.email(), requestSeller.phoneNumber());

		Seller seller = sellerRepository.save(Seller.ofRegister(requestSeller.email(), requestSeller.name(),
			passwordEncoder.encode(requestSeller.password()),
			requestSeller.address(), requestSeller.phoneNumber()));

		paymentServiceClient.createSellerBeanPay(new CreateSellerBeanPayRequest(seller.getId()));

		return SellerMapper.INSTANCE.toDto(seller);
	}

	/**
	 * 로그인 요청
	 *  <p>
	 * 로그인 요청을 보낸 유저의 고유한 상태값(역할 + 식별값 + 이메일)을 통해
	 * UniqueKey 를 만든 후  key:value(access,refresh)를 Redis에 저장
	 *  <p>
	 * @author 홍종민
	 * @param login - 사용자의 로그인 정보가 들어간 dto 입니다
	 * @return SellerDto
	 */
	public SellerDto loginRequest(LoginSellerRequest login, HttpServletResponse response) {

		final Seller seller = sellerRepository.findSellerByEmailAndIsDeletedIsFalse(login.email());

		if (seller == null || !checkIsMatchedPassword(login.password(), seller.getPassword()))
			throw new CustomException(UserErrorCode.IS_NOT_MATCHED_EMAIL_OR_PASSWORD);

		if (!seller.isValidStatus())
			throw new CustomException(UserErrorCode.IS_NOT_VALID_SELLER);

		final Set<String> authorization = Set.of(Role.SELLER.getCode());

		return SellerMapper.INSTANCE.toDto(
			sellerTokenService.createSellerTokens(seller.getId(), authorization,
				response));
	}

	/**
	 * 로그아웃 요청
	 *  <p>
	 * 로그아웃시 요청을 보낸 유저의 UniqueKey를 통해  Redis 에서 삭제
	 * 이로 인해 만약 로그아웃시 이전의 발급한 AccessToken은 유효하지않음 재발급의 대상이 됌
	 *  <p>
	 * @param authDetails - 사용자의 정보가 들어간 userDetail 입니다
	 * @author 홍종민
	 */
	public void logoutRequest(final AuthDetails authDetails) {
		sellerTokenService.removeTokens(jwtProvider.getAccessTokenKey(authDetails.getId(), authDetails.getRoll()),
			jwtProvider.getRefreshTokenKey(authDetails.getId(), authDetails.getRoll()));
	}

	/**
	 * 계좌 등록 요청
	 * @param authDetails - 사용자의 정보가 들어간 userDetail 입니다
	 * @param register - 사용자의 계좌 정보가 들어간 dto 입니다.
	 * @author 홍종민
	 */
	public AccountDto registerAccount(final AuthDetails authDetails, final CreateAccountRequest register) {

		final Seller seller = sellerRepository.findSellerByIdAndIsDeletedIsFalse(authDetails.getId());

		if (seller == null)
			throw new CustomException(UserErrorCode.NOT_FOUND_SELLER);

		final SellerAccount account = SellerAccount.ofRegister(seller, register.number(), register.bankName());

		sellerAccountRepository.save(account);

		return AccountMapper.INSTANCE.toDto(account);
	}

	/**
	 * 엑세스 토큰을 재발급 하는 메서드입니다.
	 * <p>
	 * 리프래시 토큰을 통해 엑세스 토큰을 재발급 해주는 로직입니다
	 * <p>
	 * @author Hong
	 *
	 * @param bearerToken - 리프래시토큰
	 * @param response - 쿠키를 담을 response 객체
	 * @return SellerDto
	 */
	public SellerDto reissueAccessToken(final String bearerToken, HttpServletResponse response) {

		final String refreshTokenKey = jwtProvider.getRefreshTokenKey(jwtProvider.getId(bearerToken),
			jwtProvider.getRoll(bearerToken));

		if (!redisProvider.hasKey(refreshTokenKey))
			throw new CustomException(UserErrorCode.PLEASE_RELOGIN);

		final String refreshToken = redisProvider.getData(refreshTokenKey);

		return SellerMapper.INSTANCE.toDto(
			sellerTokenService.createSellerTokens(jwtProvider.getId(refreshToken),
				Set.of(jwtProvider.getRoll(refreshToken)), response));
	}

	/**
	 * 셀러 탈퇴 로직입니다
	 * <p>
	 * 탈퇴 요청이 들어오면 해당 셀러의 정보를 확인하여 탈퇴를 하도록 하는 로직입니다.
	 * <p>
	 * @author Hong
	 *
	 * @param withdrawal- 탈퇴 요청 dto
	 * @return void
	 */
	public void withdrawSeller(final WithdrawalSellerRequest withdrawal, final AuthDetails authDetails) {

		Seller seller = sellerRepository.findSellerByIdAndIsDeletedIsFalse(authDetails.getId());

		if (seller == null || !checkIsMatchedPassword(withdrawal.password(), seller.getPassword()))
			throw new CustomException(UserErrorCode.IS_NOT_MATCHED_EMAIL_OR_PASSWORD);

		paymentServiceClient.deleteSellerBeanPay(new DeleteSellerBeanPayRequest(seller.getId()));

		if (!seller.isValidSeller(withdrawal.email(), withdrawal.phoneNumber()))
			throw new CustomException(UserErrorCode.IS_NOT_VALID_SELLER);

		seller.withdrawal();

		sellerTokenService.removeTokens(jwtProvider.getAccessTokenKey(authDetails.getId(), authDetails.getRoll()),
			jwtProvider.getRefreshTokenKey(authDetails.getId(), authDetails.getRoll()));
	}

	private void checkDuplicatedPhoneNumberOrEmail(final String email, final String phoneNumber) {
		if (sellerRepository.existsByEmailOrPhoneNumber(email, phoneNumber)) {
			throw new CustomException(UserErrorCode.DUPLICATED_EMAIL_OR_PHONENUMBER);
		}
	}

	@VisibleForTesting
	public boolean checkIsMatchedPassword(final String requestPassword, final String userPassword) {
		return passwordEncoder.matches(requestPassword, userPassword);
	}
}
