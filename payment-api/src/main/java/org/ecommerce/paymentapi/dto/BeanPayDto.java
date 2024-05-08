package org.ecommerce.paymentapi.dto;

import static org.ecommerce.paymentapi.exception.BeanPayErrorMessage.*;

import java.time.LocalDateTime;
import java.util.UUID;

import org.ecommerce.paymentapi.entity.enumerate.BeanPayStatus;
import org.ecommerce.paymentapi.entity.enumerate.ProcessStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;



@Getter
@AllArgsConstructor
public class BeanPayDto {

	private UUID id;
	private String paymentKey;
	private Integer userId;
	private Integer amount;
	private String payType;
	private String cancelReason;
	private String failReason;
	private BeanPayStatus beanPayStatus;
	private ProcessStatus processStatus;
	private LocalDateTime createDateTime;
	private LocalDateTime approveDateTime;

	public static class Request {
		public record PreCharge(
			Integer userId,
			Integer amount
		) {
		}

		public record TossFail(
			@NotNull(message = NOT_BLANK_ORDER_ID)
			UUID orderId,
			@NotBlank(message = NOT_BLANK_ERROR_CODE)
			String errorCode,
			@NotBlank(message = NOT_BLANK_ERROR_MESSAGE)
			String errorMessage
		) {
		}
	}

	public record Response(UUID beanPayId,
						   Integer userId,
						   Integer amount,
						   BeanPayStatus beanPayStatus,
						   ProcessStatus processStatus,
						   LocalDateTime createDateTime
	) {
	}
}