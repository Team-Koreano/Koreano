package org.ecommerce.orderapi.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentDto {
	private Long id;
	private Integer totalAmount;
	private LocalDateTime paymentDatetime;
	private Map<Long, PaymentDetailDto> paymentDetailDtoMap;

	public record Response(
			Long id,
			Integer totalAmount,
			LocalDateTime paymentDatetime,
			List<PaymentDetailDto.Response> paymentDetailResponses
	) {
	}
}
