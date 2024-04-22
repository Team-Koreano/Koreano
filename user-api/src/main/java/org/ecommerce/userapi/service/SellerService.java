package org.ecommerce.userapi.service;

import java.util.Set;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.userapi.dto.AccountDto;
import org.ecommerce.userapi.dto.AccountMapper;
import org.ecommerce.userapi.dto.SellerDto;
import org.ecommerce.userapi.dto.SellerMapper;
import org.ecommerce.userapi.entity.Seller;
import org.ecommerce.userapi.entity.SellerAccount;
import org.ecommerce.userapi.entity.type.Role;
import org.ecommerce.userapi.exception.UserErrorCode;
import org.ecommerce.userapi.repository.SellerAccountRepository;
import org.ecommerce.userapi.repository.SellerRepository;
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
public class SellerService {

	private final BCryptPasswordEncoder passwordEncoder;

	private final SellerRepository sellerRepository;

	private final JwtUtils jwtUtils;

	private final RedisUtils redisUtils;

	private final SellerAccountRepository sellerAccountRepository;

	//TODO : 셀러에 관한 RUD API 개발
	//TODO : 계좌에 관한 RUD API 개발

	/**
	 * 회원가입 요청
	 * @author 홍종민
	 * @param requestSeller 사용자의 생성 정보가 들어간 dto 입니다.
	 * @return SellerDto
	 */
	public SellerDto registerRequest(SellerDto.Request.Register requestSeller) {

		checkDuplicatedPhoneNumberOrEmail(requestSeller.email(),requestSeller.phoneNumber());

		final Seller seller = Seller.ofRegister(requestSeller.email(), requestSeller.name(),
			passwordEncoder.encode(requestSeller.password()),
			requestSeller.address(), requestSeller.phoneNumber());
		sellerRepository.save(seller);
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
	public SellerDto loginRequest(SellerDto.Request.Login login) {
		final Seller seller = sellerRepository.findByEmail(login.email())
			.orElseThrow(() -> new CustomException(UserErrorCode.NOT_FOUND_EMAIL));

		if (!passwordEncoder.matches(login.password(), seller.getPassword())) {
			throw new CustomException(UserErrorCode.IS_NOT_MATCHED_PASSWORD);
		}

		final Set<String> authorization = Set.of(Role.SELLER.getCode());
		final String accessToken = jwtUtils.createTokens(seller, authorization);

		return SellerMapper.INSTANCE.fromAccessToken(accessToken);
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

		final String[] authoritiesArray = authDetails.getAuthorities().stream()
			.map(GrantedAuthority::getAuthority)
			.toArray(String[]::new);

		final String roll = authoritiesArray[0];

		final String accessTokenKey = jwtUtils.getAccessTokenKey(authDetails.getUserId(), roll);

		redisUtils.deleteData(accessTokenKey);

	}

	/**
	 * 계좌 등록 요청
	 * @param authDetails - 사용자의 정보가 들어간 userDetail 입니다
	 * @param register - 사용자의 계좌 정보가 들어간 dto 입니다.
	 * @author 홍종민
	 */
	public AccountDto registerAccount(final AuthDetails authDetails, final AccountDto.Request.Register register) {
		final Seller seller = sellerRepository.findByEmail(authDetails.getEmail())
			.orElseThrow(() -> new CustomException(UserErrorCode.NOT_FOUND_EMAIL));

		final SellerAccount account = SellerAccount.ofRegister(seller, register.number(), register.bankName());

		sellerAccountRepository.save(account);

		return AccountMapper.INSTANCE.toDto(account);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void checkDuplicatedPhoneNumberOrEmail(final String email, final String phoneNumber) {
		if (sellerRepository.existsByEmailOrPhoneNumber(email,phoneNumber)) {
			throw new CustomException(UserErrorCode.DUPLICATED_EMAIL);
		}
	}
}
