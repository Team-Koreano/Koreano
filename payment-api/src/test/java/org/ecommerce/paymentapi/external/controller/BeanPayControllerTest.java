
package org.ecommerce.paymentapi.external.controller;

import static org.ecommerce.paymentapi.utils.BeanPayTimeFormatUtil.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.ecommerce.common.vo.Response;
import org.ecommerce.paymentapi.dto.PaymentDetailDto;
import org.ecommerce.paymentapi.dto.PaymentDetailMapper;
import org.ecommerce.paymentapi.dto.request.PreChargeRequest;
import org.ecommerce.paymentapi.dto.request.TossFailRequest;
import org.ecommerce.paymentapi.dto.request.TossPaymentRequest;
import org.ecommerce.paymentapi.dto.response.PaymentDetailResponse;
import org.ecommerce.paymentapi.entity.PaymentDetail;
import org.ecommerce.paymentapi.entity.UserBeanPay;
import org.ecommerce.paymentapi.entity.enumerate.PaymentStatus;
import org.ecommerce.paymentapi.entity.enumerate.ProcessStatus;
import org.ecommerce.paymentapi.external.service.BeanPayService;
import org.ecommerce.paymentapi.external.service.PaymentService;
import org.ecommerce.paymentapi.utils.PaymentTimeFormatUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

@Execution(ExecutionMode.SAME_THREAD)
@WebMvcTest(BeanPayController.class)
@MockBean(JpaMetamodelMappingContext.class)
class BeanPayControllerTest {

	@MockBean
	private BeanPayService beanPayService;

	@MockBean
	private PaymentService paymentService;

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
	void 사전결제객체_생성() throws Exception {
		//given
		final PreChargeRequest request = new PreChargeRequest(1, 10_000);
		final UserBeanPay userBeanPay = getUserBeanPay();
		final PaymentDetail entity = userBeanPay.beforeCharge(10000);
		final PaymentDetailDto dto = PaymentDetailMapper.INSTANCE.toDto(entity);

		when(beanPayService.beforeCharge(request)).thenReturn(dto);
		final Response<PaymentDetailResponse> response = new Response<>(200,
			PaymentDetailMapper.INSTANCE.toResponse(dto));

		//when
		MvcResult mvcResult =
			mvc.perform(post("/api/external/beanpay/v1/charge").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(request))).andExpect(status().isOk()).andReturn();

		//then
		String actual = mvcResult.getResponse().getContentAsString();
		String expect = mapper.writeValueAsString(response);
		assertEquals(expect, actual);
	}

	@Nested
	class 토스결제승인_검증 {
		@Test
		void 성공() throws Exception {
			//given
			final UUID orderId = UUID.randomUUID();
			final String paymentKey = "paymentKey";
			final Integer userId = 1;
			final String paymentType = "카드";

			final Integer amount = 1000;
			final String approveDateTime = "2024-04-14T17:41:52+09:00";
			final TossPaymentRequest request = new TossPaymentRequest(paymentType, paymentKey,
				orderId, amount);
			final PaymentDetailDto response = new PaymentDetailDto(
				orderId,
				1,
				1,
				1L,
				0,
				0,
				amount,
				amount,
				1,
				"paymentName",
				null,
				null,
				paymentKey,
				paymentType,
				PaymentStatus.DEPOSIT,
				ProcessStatus.COMPLETED,
				stringToDateTime(approveDateTime),
				null,
				null
			);
			final Response<PaymentDetailResponse> result = new Response<>(200,
				PaymentDetailMapper.INSTANCE.toResponse(response));

			when(beanPayService.validTossCharge(request, userId)).thenReturn(response);

			//when
			MvcResult mvcResult = mvc.perform(get("/api/external/beanpay/v1/success")
					.contentType(MediaType.APPLICATION_JSON)
					.param("orderId", String.valueOf(request.orderId()))
					.param("paymentKey", request.paymentKey())
					.param("paymentType", request.paymentType())
					.param("chargeAmount", String.valueOf(request.chargeAmount())))
				.andExpect(status().isOk())
				.andReturn();

			//then
			String actual = mvcResult.getResponse().getContentAsString();
			String expect = mapper.writeValueAsString(result);

			assertEquals(expect, actual);
		}
	}

	@Test
	void 토스사전충전_실패() throws Exception {
		//given
		final UUID orderId = UUID.randomUUID();
		final Integer userId = 1;
		final Integer amount = 1000;
		final String errorMessage = "사용자에 의해 결제가 취소되었습니다.";
		final String errorCode = "PAY_PROCESS_CANCELED";

		final TossFailRequest request = new TossFailRequest(orderId, errorCode, errorMessage);
		final UserBeanPay userBeanPay = getUserBeanPay();
		PaymentDetail paymentDetail = userBeanPay.beforeCharge(amount);
		final PaymentDetailDto response = PaymentDetailMapper.INSTANCE.toDto(paymentDetail);

		final Response<PaymentDetailResponse> result = new Response<>(200,
			PaymentDetailMapper.INSTANCE.toResponse(response));

		when(beanPayService.failTossCharge(request)).thenReturn(response);

		//when
		MvcResult mvcResult = mvc.perform(get("/api/external/beanpay/v1/fail")
				.contentType(MediaType.APPLICATION_JSON)
				.param("orderId", String.valueOf(request.orderId()))
				.param("errorCode", request.errorCode())
				.param("errorMessage", request.errorMessage()))
			.andExpect(status().isOk())
			.andReturn();

		//then
		String actual = mvcResult.getResponse().getContentAsString();
		String expect = mapper.writeValueAsString(result);

		assertEquals(expect, actual);
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
		final List<PaymentDetailDto> paymentDetailDtos = getUserPaymentDetailDtos();
		final Page<PaymentDetailDto> page = new PageImpl<>(paymentDetailDtos,
			pageable, paymentDetailDtos.size());
		final LocalDateTime startDateTime = PaymentTimeFormatUtil.stringToDateTime(startTime);
		final LocalDateTime endDateTime = PaymentTimeFormatUtil.stringToDateTime(endTime);

		given(paymentService.getUserPaymentDetailsByBetweenDate(
			anyInt(),
			eq(startDateTime),
			eq(endDateTime),
			eq(status),
			eq(pageable)
			)).willReturn(page);

		final PaymentDetailDto firstDto = paymentDetailDtos.get(0);

		//when
		mvc.perform(get("/api/external/beanpay/v1/user")
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

	@Test
	void 판매자_빈페이_내역조회() throws Exception {
		//given
		final Integer sellerId = 1;
		final String startTime = "20240505000000";
		final String endTime = "20240606000000";
		final int size = 10;
		final int pageNum = 0;
		final PaymentStatus status = PaymentStatus.PAYMENT;
		final Pageable pageable = PageRequest.of(pageNum, size);
		final List<PaymentDetailDto> paymentDetailDtos = getSellerPaymentDetailDtos();
		final Page<PaymentDetailDto> page = new PageImpl<>(paymentDetailDtos,
			pageable, paymentDetailDtos.size());
		final LocalDateTime startDateTime = PaymentTimeFormatUtil.stringToDateTime(startTime);
		final LocalDateTime endDateTime = PaymentTimeFormatUtil.stringToDateTime(endTime);

		given(paymentService.getSellerPaymentDetailByBetweenRange(
			anyInt(),
			eq(startDateTime),
			eq(endDateTime),
			eq(status),
			eq(pageable)
		)).willReturn(page);

		final PaymentDetailDto firstDto = paymentDetailDtos.get(0);

		//when
		mvc.perform(get("/api/external/beanpay/v1/seller")
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
			.andExpect(jsonPath("$.result.content[0].userId").value(firstDto.userId()))
			.andExpect(jsonPath("$.result.content[0].sellerId").value(firstDto.sellerId()))
			.andExpect(jsonPath("$.result.content[0].orderItemId").value(firstDto.orderItemId()))
			.andExpect(jsonPath("$.result.content[0].deliveryFee").value(firstDto.deliveryFee()))
			.andExpect(jsonPath("$.result.content[0].paymentAmount").value(firstDto.paymentAmount()))
			.andExpect(jsonPath("$.result.content[0].quantity").value(firstDto.quantity()))
			.andExpect(jsonPath("$.result.content[0].paymentName").value(firstDto.paymentName()))
			.andExpect(jsonPath("$.result.content[0].cancelReason").doesNotExist())
			.andExpect(jsonPath("$.result.content[0].failReason").doesNotExist())
			.andExpect(jsonPath("$.result.content[0].paymentKey").doesNotExist())
			.andExpect(jsonPath("$.result.content[0].payType").doesNotExist())
			.andExpect(jsonPath("$.result.content[0].paymentStatus").value(firstDto.paymentStatus().name()))
			.andExpect(jsonPath("$.result.content[0].processStatus").value(firstDto.processStatus().name()))
			.andExpect(jsonPath("$.result.content[0].approveDateTime").doesNotExist())
			.andExpect(jsonPath("$.result.content[0].createDateTime").value(firstDto.createDateTime().toString()))
			.andExpect(jsonPath("$.result.content[0].updateDateTime").value(firstDto.updateDateTime().toString()));
	}

	private UserBeanPay getUserBeanPay() {
		return new UserBeanPay(1, 1, 0, LocalDateTime.now(), null);
	}

	private List<PaymentDetailDto> getUserPaymentDetailDtos() {
		// 첫 번째 DTO 객체 생성
		PaymentDetailDto dto1 = new PaymentDetailDto(
			UUID.fromString("33316363-6565-3832-3636-633534663638"),
			999,
			null,
			null,
			0,
			10000,
			10000,
			0,
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
			999,
			null,
			null,
			0,
			20000,
			20000,
			0,
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
			999,
			null,
			null,
			0,
			30000,
			30000,
			0,
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

	private List<PaymentDetailDto> getSellerPaymentDetailDtos() {
		// 첫 번째 DTO 객체 생성
		PaymentDetailDto dto1 = new PaymentDetailDto(
			UUID.fromString("33316363-6565-3832-3636-633534663638"),
			999,
			1000,
			null,
			0,
			10000,
			10000,
			2000,
			5,
			"워너빈 시그니쳐 블랜디드 원두커피, 1kg, 홀빈(분쇄안함), 5개",
			null,
			null,
			null,
			null,
			PaymentStatus.PAYMENT,
			ProcessStatus.COMPLETED,
			null,
			LocalDateTime.parse("2024-06-05T18:34:31.868924"),
			LocalDateTime.parse("2024-06-05T18:34:31.868924")
		);

		// 두 번째 DTO 객체 생성
		PaymentDetailDto dto2 = new PaymentDetailDto(
			UUID.fromString("33316363-6565-3832-3636-633534663639"),
			999,
			1000,
			null,
			0,
			20000,
			20000,
			5000,
			4,
			"코맥 무표맥 커피여과지 #2, 100매입, 4개",
			null,
			null,
			null,
			null,
			PaymentStatus.PAYMENT,
			ProcessStatus.COMPLETED,
			null,
			LocalDateTime.parse("2024-06-05T18:34:31.868925"),
			LocalDateTime.parse("2024-06-05T18:34:31.868925")
		);

		// 세 번째 DTO 객체 생성
		PaymentDetailDto dto3 = new PaymentDetailDto(
			UUID.fromString("33316363-6565-3832-3636-633534663640"),
			999,
			1000,
			null,
			0,
			30000,
			30000,
			10_000,
			3,
			"곰곰 콜롬비아 블렌드 원두, 홀빈(분쇄안함), 250g, 3개",
			null,
			null,
			"31ccee8266c54f6cb50b6efa8b62e8a2",
			"카드",
			PaymentStatus.PAYMENT,
			ProcessStatus.COMPLETED,
			null,
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