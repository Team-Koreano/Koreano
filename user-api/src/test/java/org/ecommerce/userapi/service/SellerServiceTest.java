package org.ecommerce.userapi.service;

import static org.mockito.Mockito.*;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.ecommerce.common.error.CustomException;
import org.ecommerce.userapi.dto.AccountDto;
import org.ecommerce.userapi.dto.AccountMapper;
import org.ecommerce.userapi.dto.SellerDto;
import org.ecommerce.userapi.dto.SellerMapper;
import org.ecommerce.userapi.entity.Seller;
import org.ecommerce.userapi.entity.SellerAccount;
import org.ecommerce.userapi.exception.UserErrorCode;
import org.ecommerce.userapi.repository.SellerAccountRepository;
import org.ecommerce.userapi.repository.SellerRepository;
import org.ecommerce.userapi.security.AuthDetails;
import org.ecommerce.userapi.security.JwtUtils;
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
	private JwtUtils jwtUtils;

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
		final String email = "test@example.com";
		final AuthDetails authDetails = new AuthDetails(1, email, null);
		final AccountDto.Request.Register registerRequest = new AccountDto.Request.Register(
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

		when(sellerRepository.findById(authDetails.getId())).thenReturn(java.util.Optional.of(seller));
		// when
		final AccountDto result = sellerService.registerAccount(authDetails, registerRequest);
		//then
		Assertions.assertThat(result).usingRecursiveComparison().isEqualTo(dto);

	}

	@Nested
	class 셀러_등록_API {
		@Test
		void 셀러_등록() {
			//given
			SellerDto.Request.Register newSellerReqeust = new SellerDto.Request.Register(
				"newuser@example.com",
				"New User",
				"newpassword",
				"manchester",
				"01012341234");

			//when
			when(sellerRepository.existsByEmailOrPhoneNumber(newSellerReqeust.email(),
				newSellerReqeust.phoneNumber())).thenReturn(false);

			SellerDto result = sellerService.registerRequest(newSellerReqeust);

			Seller savedSeller = Seller.ofRegister(
				newSellerReqeust.email(),
				newSellerReqeust.name(),
				bCryptPasswordEncoder.encode(newSellerReqeust.password()),
				newSellerReqeust.address(),
				newSellerReqeust.phoneNumber()
			);

			SellerDto expectedResult = SellerMapper.INSTANCE.toDto(savedSeller);

			//then
			Assertions.assertThat(SellerDto.Response.Register.of(expectedResult))
				.isEqualTo(SellerDto.Response.Register.of(result));
		}

		@Test
		void 중복_이메일_케이스() {
			// given
			// 중복 이메일 케이스
			SellerDto.Request.Register duplicatedEmailRequest = new SellerDto.Request.Register("user3@example.com"
				, "Duplicate Email"
				, "password",
				"manchester"
				, "01012345678");

			//when
			when(sellerRepository.existsByEmailOrPhoneNumber(duplicatedEmailRequest.email(),
				duplicatedEmailRequest.phoneNumber())).thenReturn(true);

			//then
			Assertions.assertThatThrownBy(() -> sellerService.registerRequest(duplicatedEmailRequest))
				.isInstanceOf(CustomException.class);
		}

		@Test
		void 중복_전화번호_케이스() {
			//given
			// 중복 전화번호 케이스
			SellerDto.Request.Register duplicatedPhoneRequest = new SellerDto.Request.Register("user3@example.com"
				, "Duplicate Phone"
				, "password",
				"manchester"
				, "01099876543");

			//when
			when(sellerRepository.existsByEmailOrPhoneNumber(duplicatedPhoneRequest.email(),
				duplicatedPhoneRequest.phoneNumber())).thenReturn(true);

			//then
			Assertions.assertThatThrownBy(() -> sellerService.registerRequest(duplicatedPhoneRequest))
				.isInstanceOf(CustomException.class);
		}
	}

	@Nested
	class 셀러_로그인_API {
		@Test
		void 셀러_로그인_성공() {
			// given
			MockHttpServletResponse response = new MockHttpServletResponse();

			String email = "user1@example.com";
			String password = "password1";

			SellerDto.Request.Login loginRequest = new SellerDto.Request.Login(email, password);
			Seller seller = Seller.ofRegister(email, "John Doe", password, "어쩌구 저쩌구", "01012345678");
			when(sellerRepository.findByEmail(email)).thenReturn(Optional.of(seller));
			when(bCryptPasswordEncoder.matches(password, seller.getPassword())).thenReturn(true);
			when(jwtUtils.createSellerTokens(any(), any(), any(), any())).thenReturn("Bearer fake_access_token");
			
			// when
			SellerDto expectedResponse = sellerService.loginRequest(loginRequest, response);

			// then
			Assertions.assertThat(expectedResponse.getAccessToken()).isEqualTo("Bearer fake_access_token");
		}

		@Test
		void 셀러_로그인_실패_이메일_틀림() {
			// given
			String password = "password1";
			String incorrectEmail = "incorrect@example.com";
			MockHttpServletResponse response = new MockHttpServletResponse();

			SellerDto.Request.Login inCorrectEmailRequest = new SellerDto.Request.Login(incorrectEmail, password);

			// then
			Assertions.assertThatThrownBy(() -> sellerService.loginRequest(inCorrectEmailRequest, response))
				.isInstanceOf(CustomException.class)
				.hasMessageContaining(UserErrorCode.NOT_FOUND_EMAIL.getMessage());
		}

		@Test
		void 셀러_로그인_실패_비밀번호_틀림() {
			// given
			String email = "user1@example.com";
			String incorrectPassword = "incorrect";
			String password = "password1";

			MockHttpServletResponse response = new MockHttpServletResponse();

			SellerDto.Request.Login inCorrectPasswordRequest = new SellerDto.Request.Login(email, incorrectPassword);
			Seller seller = Seller.ofRegister(email, "John Doe", password, "어쩌구 저쩌구", "01012345678");
			when(sellerRepository.findByEmail(email)).thenReturn(Optional.of(seller));
			// then
			Assertions.assertThatThrownBy(() -> sellerService.loginRequest(inCorrectPasswordRequest, response))
				.isInstanceOf(CustomException.class)
				.hasMessageContaining(UserErrorCode.IS_NOT_MATCHED_PASSWORD.getMessage());
		}
	}
}
//TODO : 레디스로 인해 로그아웃 테스트 추후 구현

