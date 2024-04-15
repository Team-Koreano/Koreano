package org.ecommerce.orderapi.dto;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class OrderDto {

	private Long id;
	private Integer userId;
	private String receiveName;
	private String phoneNumber;
	private String address1;
	private String address2;
	private String deliveryComment;
	private Integer beanpay;
	private LocalDateTime paymentDatetime;
	private LocalDateTime orderDatetime;

	public static class Request {

		public record Create(

				@NotNull(message = "주문할 장바구니를 입력해 주세요.")
				List<Long> buckets,

				@NotBlank(message = "수신자 이름을 입력해 주세요.")
				String receiveName,

				@NotBlank(message = "휴대폰 번호를 입력해 주세요.")
				String phoneNumber,

				@NotBlank(message = "도로명 주소를 입력해 주세요.")
				String address1,

				@NotBlank(message = "상세주소를 입력해 주세요.")
				String address2,

				String deliveryComment
		) {

		}
	}

	public record Response(

			Long id,
			Integer userId,
			String receiveName,
			String phoneNumber,
			String address1,
			String address2,
			String deliveryComment,
			Integer beanpay,
			LocalDateTime paymentDatetime,
			LocalDateTime orderDatetime
	) {
		public static Response of(final OrderDto orderDto) {
			return new Response(
					orderDto.id,
					orderDto.userId,
					orderDto.receiveName,
					orderDto.phoneNumber,
					orderDto.address1,
					orderDto.address2,
					orderDto.deliveryComment,
					orderDto.beanpay,
					orderDto.paymentDatetime,
					orderDto.orderDatetime
			);
		}
	}
}
