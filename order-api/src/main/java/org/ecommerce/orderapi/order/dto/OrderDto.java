package org.ecommerce.orderapi.order.dto;

import static org.ecommerce.orderapi.order.exception.ErrorMessage.*;

import java.time.LocalDateTime;
import java.util.List;

import org.ecommerce.orderapi.order.entity.enumerated.OrderStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderDto {

	private Long id;
	private Integer userId;
	private String userName;
	private String receiveName;
	private String phoneNumber;
	private String address1;
	private String address2;
	private String deliveryComment;
	private Integer totalPaymentAmount;
	private OrderStatus status;
	private LocalDateTime statusDateTime;
	private LocalDateTime paymentDatetime;
	private LocalDateTime orderDatetime;
	private List<OrderItemDto> orderItemDtos;

	public static class Request {

		public record Create(

				@NotNull(message = BUCKET_IDS_NOT_NULL)
				@Size(min = 1, max = 15, message = INVALID_BUCKET_SIZE)
				List<Long> bucketIds,

				@NotBlank(message = RECEIVE_NAME_NOT_BLANK)
				String receiveName,

				@NotBlank(message = PHONE_NUMBER_NOT_BLANK)
				String phoneNumber,

				@NotBlank(message = ADDRESS1_NOT_BLANK)
				String address1,

				@NotBlank(message = ADDRESS2_NOT_BLANK)
				String address2,

				String deliveryComment
		) {
		}
	}

	public record Response(
			Long id,
			Integer userId,
			String userName,
			String receiveName,
			String phoneNumber,
			String address1,
			String address2,
			String deliveryComment,
			Integer totalPaymentAmount,
			OrderStatus status,
			LocalDateTime statusDateTime,
			LocalDateTime paymentDatetime,
			LocalDateTime orderDatetime,
			List<OrderItemDto.Response> orderItemResponses
	) {
	}
}
