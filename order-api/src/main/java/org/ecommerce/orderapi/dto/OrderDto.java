package org.ecommerce.orderapi.dto;

import static org.ecommerce.orderapi.exception.ErrorMessage.*;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
	private LocalDateTime paymentDatetime;
	private LocalDateTime orderDatetime;
	private List<OrderDetailDto> orderDetailDtos;

	public static class Request {

		public record Place(

				@NotNull(message = BUCKET_IDS_NOT_NULL)
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
			LocalDateTime paymentDatetime,
			LocalDateTime orderDatetime,
			List<OrderDetailDto.Response> orderDetailResponses
	) {
	}
}
