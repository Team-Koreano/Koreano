package org.ecommerce.userapi.dto;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

import org.ecommerce.userapi.entity.Seller;
import org.ecommerce.userapi.entity.enumerated.UserStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SellerDtoTest {
	private static final LocalDateTime CREATE_DATE = LocalDateTime.now();
	private static final Seller SELLER = new Seller(
		1,
		"temp@temp.com",
		"user",
		"password",
		"myHome",
		"010-0000-0000",
		CREATE_DATE,
		false,
		null,
		0L,
		UserStatus.GENERAL
	);

	@Test
	void 셀러_응답() {
		//given
		final SellerDto sellerDto = SellerMapper.INSTANCE.sellerToDto(SELLER);
		//when
		final SellerDto.Response.Register register = SellerMapper.INSTANCE.sellerDtoToResponse(sellerDto);
		//then
		assertThat(register.email()).isEqualTo(SELLER.getEmail());
		assertThat(register.name()).isEqualTo(SELLER.getName());
		assertThat(register.address()).isEqualTo(SELLER.getAddress());
		assertThat(register.phoneNumber()).isEqualTo(SELLER.getPhoneNumber());
	}
}