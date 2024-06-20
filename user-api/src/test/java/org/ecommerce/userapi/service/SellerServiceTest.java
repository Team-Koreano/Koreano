package org.ecommerce.userapi.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;

import org.ecommerce.common.error.CustomException;
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
import org.ecommerce.userapi.entity.enumerated.UserStatus;
import org.ecommerce.userapi.exception.UserErrorCode;
import org.ecommerce.userapi.external.service.SellerService;
import org.ecommerce.userapi.provider.JwtProvider;
import org.ecommerce.userapi.repository.SellerAccountRepository;
import org.ecommerce.userapi.repository.SellerRepository;
import org.ecommerce.userapi.security.AuthDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
class SellerServiceTest {

	@InjectMocks
	private SellerService sellerService;

	@Mock
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Mock
	private SellerRepository sellerRepository;

	@Mock
	private SellerAccountRepository sellerAccountRepository;

	@Mock
	private JwtProvider jwtProvider;

	@Mock
	private PaymentServiceClient paymentServiceClient;

	@BeforeEach
	public void 기초_셋팅() {
		Seller seller = Seller.ofRegister(
			"test@example.com",
			"Jane Smith",
			"test",
			"어쩌구_저쩌구",
			"010-0000-0000"
		);
		sellerRepository.save(seller);
	}

	@Test
	void 셀러_계좌_등록() {
		// given
		final AuthDetails authDetails = new AuthDetails(1, null);
		final CreateAccountRequest registerRequest = new CreateAccountRequest(
			"213124124123", "부산은행");

		final Seller seller = Seller.ofRegister(
			"test@example.com",
			"Jane Smith",
			"test",
			"부산시 북구",
			"01087654321"
		);

		final SellerAccount account = SellerAccount.ofRegister(seller, registerRequest.number(),
			registerRequest.bankName());

		final AccountDto dto = AccountMapper.INSTANCE.toDto(account);

		when(sellerRepository.findSellerByIdAndIsDeletedIsFalse(authDetails.getId())).thenReturn(seller);
		// when
		final AccountDto result = sellerService.registerAccount(authDetails, registerRequest);
		//then
		assertThat(result).usingRecursiveComparison().isEqualTo(dto);

	}

	@Nested
	class 셀러_등록_API {
		@Test
		void 셀러_등록() {
			//given
			final CreateSellerRequest createSellerRequest = new CreateSellerRequest(
				"newuser@example.com",
				"New User",
				"newpassword",
				"manchester",
				"01012341234");

			//when
			when(sellerRepository.existsByEmailOrPhoneNumber(createSellerRequest.email(),
				createSellerRequest.phoneNumber())).thenReturn(false);
			final Seller entity = new Seller(
				1,
				createSellerRequest.email(),
				createSellerRequest.name(),
				createSellerRequest.password(),
				createSellerRequest.address(),
				createSellerRequest.phoneNumber(),
				LocalDateTime.now(),
				false,
				LocalDateTime.now(),
				UserStatus.GENERAL,
				null
			);

			when(sellerRepository.save(any(Seller.class))).thenReturn(entity);

			final SellerDto result = sellerService.registerRequest(createSellerRequest);

			final Seller savedSeller = Seller.ofRegister(
				createSellerRequest.email(),
				createSellerRequest.name(),
				bCryptPasswordEncoder.encode(createSellerRequest.password()),
				createSellerRequest.address(),
				createSellerRequest.phoneNumber()
			);

			final SellerDto expectedResult = SellerMapper.INSTANCE.toDto(savedSeller);

			//then
			verify(paymentServiceClient, times(1)).createSellerBeanPay(
				new CreateSellerBeanPayRequest(entity.getId()));
			assertThat(SellerMapper.INSTANCE.toResponse(expectedResult))
				.isEqualTo(SellerMapper.INSTANCE.toResponse(result));
		}

		@Test
		void 중복_이메일_케이스() {
			// given
			// 중복 이메일 케이스
			final CreateSellerRequest duplicatedEmailRequest = new CreateSellerRequest("user3@example.com"
				, "Duplicate Email"
				, "password",
				"manchester"
				, "01012345678");

			//when
			when(sellerRepository.existsByEmailOrPhoneNumber(duplicatedEmailRequest.email(),
				duplicatedEmailRequest.phoneNumber())).thenReturn(true);

			//then
			assertThatThrownBy(() -> sellerService.registerRequest(duplicatedEmailRequest))
				.isInstanceOf(CustomException.class);
		}

		@Test
		void 중복_전화번호_케이스() {
			//given
			// 중복 전화번호 케이스
			final CreateSellerRequest duplicatedPhoneRequest = new CreateSellerRequest("user3@example.com"
				, "Duplicate Phone"
				, "password",
				"manchester"
				, "01099876543");

			//when
			when(sellerRepository.existsByEmailOrPhoneNumber(duplicatedPhoneRequest.email(),
				duplicatedPhoneRequest.phoneNumber())).thenReturn(true);

			//then
			assertThatThrownBy(() -> sellerService.registerRequest(duplicatedPhoneRequest))
				.isInstanceOf(CustomException.class);
		}
	}

	@Nested
	class 셀러_로그인_API {
		@Test
		void 셀러_로그인_성공() {
			// given
			MockHttpServletResponse response = new MockHttpServletResponse();

			final String email = "user1@example.com";
			final String password = "password1";

			final LoginSellerRequest loginRequest = new LoginSellerRequest(email, password);
			final Seller seller = Seller.ofRegister(email, "John Doe", password, "어쩌구 저쩌구", "01012345678");
			when(sellerRepository.findSellerByEmailAndIsDeletedIsFalse(email)).thenReturn(seller);
			when(jwtProvider.createSellerTokens(any(), any(), any())).thenReturn("Bearer fake_access_token");
			when(sellerService.checkIsMatchedPassword(password, seller.getPassword())).thenReturn(true);

			// when
			final SellerDto expectedResponse = sellerService.loginRequest(loginRequest, response);

			// then
			assertThat(expectedResponse.accessToken()).isEqualTo("Bearer fake_access_token");
		}

		@Test
		void 셀러_로그인_실패_이메일_틀림() {
			// given
			final String password = "password1";
			final String incorrectEmail = "incorrect@example.com";
			MockHttpServletResponse response = new MockHttpServletResponse();

			final LoginSellerRequest inCorrectEmailRequest = new LoginSellerRequest(incorrectEmail, password);

			// then
			assertThatThrownBy(() -> sellerService.loginRequest(inCorrectEmailRequest, response))
				.isInstanceOf(CustomException.class)
				.hasMessageContaining(UserErrorCode.IS_NOT_MATCHED_EMAIL_OR_PASSWORD.getMessage());
		}

		@Test
		void 셀러_로그인_실패_비밀번호_틀림() {
			// given
			final String email = "user1@example.com";
			final String incorrectPassword = "incorrect";
			final String password = "password1";

			MockHttpServletResponse response = new MockHttpServletResponse();

			final LoginSellerRequest inCorrectPasswordRequest = new LoginSellerRequest(email,
				incorrectPassword);
			final Seller seller = Seller.ofRegister(email, "John Doe", password, "어쩌구 저쩌구", "01012345678");

			when(sellerRepository.findSellerByEmailAndIsDeletedIsFalse(email)).thenReturn(seller);
			// then
			assertThatThrownBy(() -> sellerService.loginRequest(inCorrectPasswordRequest, response))
				.isInstanceOf(CustomException.class)
				.hasMessageContaining(UserErrorCode.IS_NOT_MATCHED_EMAIL_OR_PASSWORD.getMessage());
		}
	}

	@Nested
	class 셀러_탈퇴_API {
		@Test
		void 셀러_탈퇴_성공() {
			// given
			final String email = "user1@example.com";
			final String phoneNumber = "01012345678";
			final String password = "password1";
			final AuthDetails authDetails = new AuthDetails(1, null);

			final WithdrawalSellerRequest withdrawalRequest = new WithdrawalSellerRequest(email, password,
				phoneNumber);
			final Seller seller = Seller.ofRegister(email, "John Doe", password, "어쩌구 저쩌구", phoneNumber);
			final SellerAccount account = SellerAccount.ofRegister(seller, "1234567890", "KEB하나은행");

			when(sellerRepository.findSellerByIdAndIsDeletedIsFalse(authDetails.getId()))
				.thenReturn(seller);
			when(sellerService.checkIsMatchedPassword(password, seller.getPassword())).thenReturn(true);

			// when
			sellerService.withdrawSeller(withdrawalRequest, authDetails);

			// then
			verify(sellerRepository, times(1)).findSellerByIdAndIsDeletedIsFalse(authDetails.getId());
			verify(paymentServiceClient, times(1)).deleteSellerBeanPay(
				new DeleteSellerBeanPayRequest(seller.getId()));
			assertThat(seller.isValidStatus()).isFalse();
			assertThat(seller.isDeleted()).isTrue();
		}

		@Test
		void 회원_탈퇴_실패_이메일_또는_전화번호_틀림() {
			// given
			final String incorrectEmail = "incorrect@example.com";
			final String incorrectPhoneNumber = "01011112222";
			final String password = "password1";

			final WithdrawalSellerRequest withdrawalRequest = new WithdrawalSellerRequest(incorrectEmail,
				password, incorrectPhoneNumber);
			final AuthDetails authDetails = new AuthDetails(1, null);

			// then
			assertThatThrownBy(() -> sellerService.withdrawSeller(withdrawalRequest, authDetails))
				.isInstanceOf(CustomException.class)
				.hasMessageContaining(UserErrorCode.IS_NOT_MATCHED_EMAIL_OR_PASSWORD.getMessage());
		}

		@Test
		void 회원_탈퇴_실패_비밀번호_틀림() {
			// given
			final String email = "user1@example.com";
			final String phoneNumber = "01012345678";
			final String incorrectPassword = "incorrectPassword";
			final String correctPassword = "correctPassword";
			final WithdrawalSellerRequest withdrawalRequest = new WithdrawalSellerRequest(email,
				incorrectPassword, phoneNumber);
			final AuthDetails authDetails = new AuthDetails(1, null);

			final Seller seller = Seller.ofRegister(email, "John Doe", correctPassword, "어쩌구 저쩌구", phoneNumber);

			when(sellerRepository.findSellerByIdAndIsDeletedIsFalse(authDetails.getId())).thenReturn(
				seller);
			when(sellerService.checkIsMatchedPassword(incorrectPassword, seller.getPassword())).thenReturn(false);
			// then
			assertThatThrownBy(() -> sellerService.withdrawSeller(withdrawalRequest, authDetails))
				.isInstanceOf(CustomException.class)
				.hasMessageContaining(UserErrorCode.IS_NOT_MATCHED_EMAIL_OR_PASSWORD.getMessage());
		}
	}
}
//TODO : 레디스로 인해 로그아웃 테스트 추후 구현

