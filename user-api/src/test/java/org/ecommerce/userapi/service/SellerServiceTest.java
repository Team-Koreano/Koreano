package org.ecommerce.userapi.service;

import static org.mockito.Mockito.*;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.ecommerce.common.error.CustomException;
import org.ecommerce.userapi.dto.SellerDto;
import org.ecommerce.userapi.dto.SellerMapper;
import org.ecommerce.userapi.entity.Seller;
import org.ecommerce.userapi.exception.UserErrorCode;
import org.ecommerce.userapi.repository.SellerRepository;
import org.ecommerce.userapi.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
	void 셀러_등록() {
		//given
		SellerDto.Request.Register newSellerReqeust = new SellerDto.Request.Register(
			"newuser@example.com",
			"New User",
			"newpassword",
			"manchester",
			"01012341234");

		//when
		when(sellerRepository.existsByEmail(newSellerReqeust.email())).thenReturn(false);
		when(sellerRepository.existsByPhoneNumber(newSellerReqeust.phoneNumber())).thenReturn(false);

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
		Assertions.assertThat(SellerDto.Response.Register.of(expectedResult)).isEqualTo(SellerDto.Response.Register.of(result));

		// given
		// 중복 이메일 케이스
		SellerDto.Request.Register duplicatedEmailRequest = new SellerDto.Request.Register("user3@example.com"
			,"Duplicate Email"
			,"password",
			"manchester"
			,"01012345678");

		//when
		when(sellerRepository.existsByEmail(duplicatedEmailRequest.email())).thenReturn(true);

		//then
		Assertions.assertThatThrownBy(() -> sellerService.registerRequest(duplicatedEmailRequest))
			.isInstanceOf(CustomException.class);

		//given
		// 중복 전화번호 케이스
		SellerDto.Request.Register duplicatedPhoneRequest = new SellerDto.Request.Register("user3@example.com"
			,"Duplicate Phone"
			,"password",
			"manchester"
			,"01099876543");

		//when
		when(sellerRepository.existsByEmail(duplicatedPhoneRequest.email())).thenReturn(false);
		when(sellerRepository.existsByPhoneNumber(duplicatedPhoneRequest.phoneNumber())).thenReturn(true);

		//then
		Assertions.assertThatThrownBy(() -> sellerService.registerRequest(duplicatedPhoneRequest))
			.isInstanceOf(CustomException.class);
	}

	@Test
	void 셀러_로그인() {
		// given
		String email = "user1@example.com";
		String password = "password1";

		SellerDto.Request.Login loginRequest = new SellerDto.Request.Login(email, password);
		Seller seller = Seller.ofRegister(email, "John Doe", password, "어쩌구 저쩌구", "01012345678");
		when(sellerRepository.findByEmail(email)).thenReturn(Optional.of(seller));
		when(bCryptPasswordEncoder.matches(password, seller.getPassword())).thenReturn(true);

		//when
		when(jwtUtils.createTokens(any(), any())).thenReturn("Bearer fake_token");

		//then
		SellerDto response  = sellerService.loginRequest(loginRequest);
		Assertions.assertThat(response.getAccessToken()).isEqualTo("Bearer fake_token");

		//이메일이 틀릴 경우
		String incorrectEmail = "incorrect@example.com";
		SellerDto.Request.Login inCorrectEmailRequest = new SellerDto.Request.Login(incorrectEmail, password);

		//then
		Assertions.assertThatThrownBy(() -> sellerService.loginRequest(inCorrectEmailRequest))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(UserErrorCode.NOT_FOUND_EMAIL.getMessage());

		//비밀번호 틀릴 경우
		String incorrectPassword = "incorrect";

		//then
		SellerDto.Request.Login inCorrectPasswordRequest = new SellerDto.Request.Login(email, incorrectPassword);
		Assertions.assertThatThrownBy(() -> sellerService.loginRequest(inCorrectPasswordRequest))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(UserErrorCode.IS_NOT_MATCHED_PASSWORD.getMessage());

	}
}
//TODO : 레디스로 인해 로그아웃 테스트 추후 구현

