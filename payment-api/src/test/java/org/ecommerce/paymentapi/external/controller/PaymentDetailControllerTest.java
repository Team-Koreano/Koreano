package org.ecommerce.paymentapi.external.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.ecommerce.paymentapi.dto.PaymentDetailDto;
import org.ecommerce.paymentapi.entity.enumerate.PaymentStatus;
import org.ecommerce.paymentapi.entity.enumerate.ProcessStatus;
import org.ecommerce.paymentapi.external.service.PaymentDetailReadService;
import org.ecommerce.paymentapi.utils.PaymentTimeFormatUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

@Execution(ExecutionMode.SAME_THREAD)
@WebMvcTest(PaymentDetailController.class)
@MockBean(JpaMetamodelMappingContext.class)
class PaymentDetailControllerTest {

	@MockBean
	private PaymentDetailReadService paymentDetailReadService;

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private WebApplicationContext wac;

	@BeforeEach
	void setup() {
		this.mvc = MockMvcBuilders.webAppContextSetup(wac)
			.addFilter(new CharacterEncodingFilter("UTF-8", true))
			.build();
	}


	@Test
	void 구매자_빈페이_내역조회() throws Exception {
		//given
		final Integer userId = 1;
		final String startTime = "20240505000000";
		final String endTime = "20240606000000";
		final int size = 10;
		final int pageNum = 0;
		final PaymentStatus status = PaymentStatus.DEPOSIT;
		final Pageable pageable = PageRequest.of(pageNum, size);
		final List<PaymentDetailDto> paymentDetailDtos = getPaymentDetailDtos();
		final Page<PaymentDetailDto> page = new PageImpl<>(paymentDetailDtos,
			pageable, 3);
		final LocalDateTime startDateTime = PaymentTimeFormatUtil.stringToDateTime(startTime);
		final LocalDateTime endDateTime = PaymentTimeFormatUtil.stringToDateTime(endTime);

		given(paymentDetailReadService.getPaymentDetailsByDateRange(
			anyInt(),
			eq(startDateTime),
			eq(endDateTime),
			any(PaymentStatus.class),
			any(Pageable.class)
		)).willReturn(page);

		final PaymentDetailDto firstDto = paymentDetailDtos.get(0);

		//when
		mvc.perform(get("/api/external/paymentdetail/v1")
				.param("startDateTime", startTime)
				.param("endDateTime", endTime)
				.param("status", status.name())
				.param("size", String.valueOf(size))
				.param("page", String.valueOf(pageNum))
				.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.result.content[0].id").value(firstDto.id().toString()))
			.andExpect(jsonPath("$.result.content[0].paymentDetailId").value(firstDto.paymentDetailId()))
			.andExpect(jsonPath("$.result.content[0].userId").value(firstDto.userId()))
			.andExpect(jsonPath("$.result.content[0].sellerId").value(firstDto.sellerId()))
			.andExpect(jsonPath("$.result.content[0].orderItemId").value(firstDto.orderItemId()))
			.andExpect(jsonPath("$.result.content[0].deliveryFee").value(firstDto.deliveryFee()))
			.andExpect(jsonPath("$.result.content[0].paymentAmount").value(firstDto.paymentAmount()))
			.andExpect(jsonPath("$.result.content[0].quantity").value(firstDto.quantity()))
			.andExpect(jsonPath("$.result.content[0].paymentName").value(firstDto.paymentName()))
			.andExpect(jsonPath("$.result.content[0].cancelReason").doesNotExist())
			.andExpect(jsonPath("$.result.content[0].failReason").doesNotExist())
			.andExpect(jsonPath("$.result.content[0].paymentKey").value(firstDto.paymentKey()))
			.andExpect(jsonPath("$.result.content[0].payType").value(firstDto.payType()))
			.andExpect(jsonPath("$.result.content[0].paymentStatus").value(firstDto.paymentStatus().name()))
			.andExpect(jsonPath("$.result.content[0].processStatus").value(firstDto.processStatus().name()))
			.andExpect(jsonPath("$.result.content[0].approveDateTime").value(firstDto.approveDateTime().toString()))
			.andExpect(jsonPath("$.result.content[0].createDateTime").value(firstDto.createDateTime().toString()))
			.andExpect(jsonPath("$.result.content[0].updateDateTime").value(firstDto.updateDateTime().toString()));
	}

	private List<PaymentDetailDto> getPaymentDetailDtos() {
		// 첫 번째 DTO 객체 생성
		PaymentDetailDto dto1 = new PaymentDetailDto(
			UUID.fromString("33316363-6565-3832-3636-633534663638"),
			null,
			999,
			null,
			null,
			0,
			10000,
			0,
			"1만원 충전",
			null,
			null,
			"31ccee8266c54f6cb50b6efa8b62e8a0",
			"카드",
			PaymentStatus.DEPOSIT,
			ProcessStatus.COMPLETED,
			LocalDateTime.parse("2024-06-05T18:34:31.867813"),
			LocalDateTime.parse("2024-06-05T18:34:31.868924"),
			LocalDateTime.parse("2024-06-05T18:34:31.868924")
		);

		// 두 번째 DTO 객체 생성
		PaymentDetailDto dto2 = new PaymentDetailDto(
			UUID.fromString("33316363-6565-3832-3636-633534663639"),
			null,
			999,
			null,
			null,
			0,
			20000,
			0,
			"2만원 충전",
			null,
			null,
			"31ccee8266c54f6cb50b6efa8b62e8a1",
			"카드",
			PaymentStatus.DEPOSIT,
			ProcessStatus.COMPLETED,
			LocalDateTime.parse("2024-06-05T18:34:31.867814"),
			LocalDateTime.parse("2024-06-05T18:34:31.868925"),
			LocalDateTime.parse("2024-06-05T18:34:31.868925")
		);

		// 세 번째 DTO 객체 생성
		PaymentDetailDto dto3 = new PaymentDetailDto(
			UUID.fromString("33316363-6565-3832-3636-633534663640"),
			null,
			999,
			null,
			null,
			0,
			30000,
			0,
			"3만원 충전",
			null,
			null,
			"31ccee8266c54f6cb50b6efa8b62e8a2",
			"카드",
			PaymentStatus.DEPOSIT,
			ProcessStatus.COMPLETED,
			LocalDateTime.parse("2024-06-05T18:34:31.867815"),
			LocalDateTime.parse("2024-06-05T18:34:31.868926"),
			LocalDateTime.parse("2024-06-05T18:34:31.868926")
		);
		return List.of(
			dto1,
			dto2,
			dto3
		);
	}
}