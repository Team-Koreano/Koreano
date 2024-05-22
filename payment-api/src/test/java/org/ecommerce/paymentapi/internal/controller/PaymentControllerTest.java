package org.ecommerce.paymentapi.internal.controller;

import static org.ecommerce.paymentapi.entity.enumerate.Role.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import org.ecommerce.paymentapi.dto.PaymentDetailDto;
import org.ecommerce.paymentapi.dto.PaymentDetailDto.Request.PaymentCancel;
import org.ecommerce.paymentapi.dto.PaymentDto;
import org.ecommerce.paymentapi.dto.PaymentMapper;
import org.ecommerce.paymentapi.entity.BeanPay;
import org.ecommerce.paymentapi.entity.Payment;
import org.ecommerce.paymentapi.entity.PaymentDetail;
import org.ecommerce.paymentapi.internal.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(PaymentController.class)
@MockBean(JpaMetamodelMappingContext.class)
class PaymentControllerTest {

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

	@Nested
	class 결제 {
		@Test
		void 성공() throws Exception {
			//given
			final Long orderId = 1L;
			final Integer startAmount = 100_000;
			final Integer userId = 1;
			final BeanPay userBeanPay = new BeanPay(1, userId, USER, startAmount, LocalDateTime.now());
			final Integer[] sellerIds = new Integer[] {1, 2};
			final Long[] orderItemIds = new Long[] {1L, 2L, 3L};
			final Integer paymentAmount = 15000;
			final Integer[] amounts = {5000, 5000, 5000};
			final Integer[] deliveryFees = {0, 0, 0};
			final String orderName = "orderName";
			final Integer[] quantity = {3, 3, 3};
			final Integer[] prices = {1000, 1000, 1000};
			final String[] productNames = new String[]{"product1", "product2",
				"product3"};
			final List<BeanPay> sellerBeanPays = List.of(
				new BeanPay(2, 1, SELLER, 0, LocalDateTime.now()),
				new BeanPay(3, 2, SELLER, 0, LocalDateTime.now())
			);
			final PaymentDto.Request.PaymentPrice request =
				new PaymentDto.Request.PaymentPrice(
				1L,
				paymentAmount,
				userBeanPay.getUserId(),
				orderName,
				List.of(
					new PaymentDetailDto.Request.PaymentDetailPrice(
						orderItemIds[0],
						amounts[0],
						prices[0],
						quantity[0],
						deliveryFees[0],
						sellerIds[0],
						productNames[0]
					),
					new PaymentDetailDto.Request.PaymentDetailPrice(
						orderItemIds[1],
						amounts[1],
						prices[1],
						quantity[1],
						deliveryFees[1],
						sellerIds[1],
						productNames[1]
					),
					new PaymentDetailDto.Request.PaymentDetailPrice(
						orderItemIds[2],
						amounts[2],
						prices[2],
						quantity[2],
						deliveryFees[2],
						sellerIds[0],
						productNames[2]
					)
				)
			);

			final List<Pair<BeanPay, PaymentDetailDto.Request.PaymentDetailPrice>> beanPayPaymentPrice = PaymentService.mappedBeanPayPaymentDetailPrice(
				request, sellerBeanPays);
			final Payment payment = Payment.ofPayment(
				userBeanPay,
				orderId,
				paymentAmount,
				orderName,
				beanPayPaymentPrice
			);
			final PaymentDto paymentDto = PaymentMapper.INSTANCE.paymentToDto(payment);
			final PaymentDto.Response response = PaymentMapper.INSTANCE.paymentDtoToResponse(paymentDto);
			when(paymentService.paymentPrice(request)).thenReturn(paymentDto);

			//when
			mvc.perform(post("/api/internal/payment/v1")
					.contentType(MediaType.APPLICATION_JSON)
					.content(mapper.writeValueAsString(request))
				).andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(response.id()))
				.andExpect(jsonPath("$.orderId").value(response.orderId()))
				.andExpect(jsonPath("$.userId").value(response.userId()))
				.andExpect(jsonPath("$.totalAmount").value(response.totalAmount()))
				.andExpect(jsonPath("$.orderName").value(response.orderName()))
				.andExpect(jsonPath("$.processStatus").value(response.processStatus().name()))
				.andExpect(jsonPath("$.createDateTime").value(response.createDateTime()))
				.andExpect(jsonPath("$.isVisible").value(response.isVisible()));

			//then

		}


	}

	@Nested
	class 환불 {
		@Test
		void 성공() throws Exception {
			//given
			final Long orderId = 1L;
			final String cancelReason = "사용자 단순 변심";
			final Integer startAmount = 100_000;
			final Integer userId = 1;
			final BeanPay userBeanPay = new BeanPay(1, userId, USER, startAmount, LocalDateTime.now());
			final Integer[] sellerIds = new Integer[] {1, 2};
			final Long[] orderItemIds = new Long[] {1L, 2L, 3L};
			final Integer paymentAmount = 15000;
			final Integer[] amounts = {5000, 5000, 5000};
			final Integer[] deliveryFees = {0, 0, 0};
			final String orderName = "orderName";
			final Integer[] quantity = {3, 3, 3};
			final Integer[] prices = {1000, 1000, 1000};
			final String[] productNames = new String[]{"product1", "product2",
				"product3"};
			final List<BeanPay> sellerBeanPays = List.of(
				new BeanPay(2, 1, SELLER, 0, LocalDateTime.now()),
				new BeanPay(3, 2, SELLER, 0, LocalDateTime.now())
			);
			final PaymentDto.Request.PaymentPrice paymentPrice =
				new PaymentDto.Request.PaymentPrice(
					1L,
					paymentAmount,
					userBeanPay.getUserId(),
					orderName,
					List.of(
						new PaymentDetailDto.Request.PaymentDetailPrice(
							orderItemIds[0],
							amounts[0],
							prices[0],
							quantity[0],
							deliveryFees[0],
							sellerIds[0],
							productNames[0]
						),
						new PaymentDetailDto.Request.PaymentDetailPrice(
							orderItemIds[1],
							amounts[1],
							prices[1],
							quantity[1],
							deliveryFees[1],
							sellerIds[1],
							productNames[1]
						),
						new PaymentDetailDto.Request.PaymentDetailPrice(
							orderItemIds[2],
							amounts[2],
							prices[2],
							quantity[2],
							deliveryFees[2],
							sellerIds[0],
							productNames[2]
						)
					)
				);

			List<Pair<BeanPay, PaymentDetailDto.Request.PaymentDetailPrice>> beanPayPaymentPrice = PaymentService.mappedBeanPayPaymentDetailPrice(
				paymentPrice, sellerBeanPays);
			Payment payment = Payment.ofPayment(
				userBeanPay,
				orderId,
				paymentAmount,
				orderName,
				beanPayPaymentPrice
			);

			PaymentDetail paymentDetail = payment.getPaymentDetails().get(0);
			PaymentCancel request = new PaymentCancel(
				paymentDetail.getUserBeanPay().getUserId(),
				paymentDetail.getSellerBeanPay().getUserId(),
				paymentDetail.getOrderItemId(),
				cancelReason
			);
			paymentDetail.cancelPaymentDetail(cancelReason);
			PaymentDetailDto dto = PaymentMapper.INSTANCE.paymentDetailToDto(
				paymentDetail);
			when(paymentService.cancelPaymentDetail(request)).thenReturn(dto);

			//when
			mvc.perform(delete("/api/internal/payment/v1")
					.contentType(MediaType.APPLICATION_JSON)
					.content(mapper.writeValueAsString(request))
				).andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.userId").value(userId))
				.andExpect(jsonPath("$.sellerId").value(paymentDetail.getSellerBeanPay().getUserId()))
				.andExpect(jsonPath("$.orderItemId").value(paymentDetail.getOrderItemId()))
				.andExpect(jsonPath("$.deliveryFee").value(paymentDetail.getDeliveryFee()))
				.andExpect(jsonPath("$.paymentAmount").value(paymentDetail.getPaymentAmount()))
				.andExpect(jsonPath("$.quantity").value(paymentDetail.getQuantity()))
				.andExpect(jsonPath("$.paymentName").value(paymentDetail.getPaymentName()))
				.andExpect(jsonPath("$.cancelReason").value(paymentDetail.getCancelReason()))
				.andExpect(jsonPath("$.failReason").value(paymentDetail.getFailReason()))
				.andExpect(jsonPath("$.paymentKey").doesNotExist())
				.andExpect(jsonPath("$.payType").doesNotExist())
				.andExpect(jsonPath("$.paymentStatus").value(paymentDetail.getPaymentStatus().name()))
				.andExpect(jsonPath("$.processStatus").value(paymentDetail.getProcessStatus().name()))
				.andExpect(jsonPath("$.createDateTime").value(paymentDetail.getCreateDateTime()))
				.andExpect(jsonPath("$.updateDateTime").value(paymentDetail.getUpdateDateTime()));


			//then

		}
	}


}