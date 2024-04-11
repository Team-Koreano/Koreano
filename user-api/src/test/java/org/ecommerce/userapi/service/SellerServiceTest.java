package org.ecommerce.userapi.service;

import static org.mockito.Mockito.*;

import org.assertj.core.api.Assertions;
import org.ecommerce.common.error.CustomException;
import org.ecommerce.userapi.dto.SellerDto;
import org.ecommerce.userapi.entity.Seller;
import org.ecommerce.userapi.repository.SellerRepository;
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

		SellerDto.Response.Register response = sellerService.registerRequest(newSellerReqeust);

		Seller savedSeller = Seller.ofRegister(
			newSellerReqeust.email(),
			newSellerReqeust.name(),
			bCryptPasswordEncoder.encode(newSellerReqeust.password()),
			newSellerReqeust.address(),
			newSellerReqeust.phoneNumber()
		);

		//then
		Assertions.assertThat(response).isEqualTo(SellerDto.Response.Register.of(savedSeller));

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
}