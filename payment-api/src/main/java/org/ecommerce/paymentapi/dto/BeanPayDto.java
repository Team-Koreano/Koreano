package org.ecommerce.paymentapi.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import org.ecommerce.paymentapi.entity.BeanPay;
import org.ecommerce.paymentapi.entity.type.BeanPayStatus;
import org.ecommerce.paymentapi.entity.type.ProcessStatus;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

public class BeanPayDto {
	public static class Request {
		public record PreCharge(
			Integer userId,
			Integer amount
		) {
		}

		public record TossPayment(
			@NotBlank(message = "결제타입을 입력해주세요")
			String paymentType,

			@NotBlank(message = "결제키을 입력해주세요")
			@Size(min = 6, message = "최소 6자 이상 입력해주세요")
			String paymentKey,

			@NotNull(message = "충전ID를 입력해주세요")
			UUID orderId,

			@Min(value = 0, message = "최소값은 0입니다.")
			Integer amount
		) {
		}

		public record TossFail(
			String errorCode,
			String errorMessage
		) {
		}
	}

	public record Response(UUID beanPayId,
						   Integer userId,
						   Integer amount,
						   BeanPayStatus beanPayStatus,
						   ProcessStatus processStatus,
						   LocalDateTime createDateTime) {

		public static BeanPayDto.Response ofCreate(BeanPay beanPay) {
			return new BeanPayDto.Response(beanPay.getId(),
				beanPay.getUserId(),
				beanPay.getAmount(),
				beanPay.getBeanPayStatus(),
				beanPay.getProcessStatus(),
				beanPay.getCreateDateTime());
		}

		@JsonIgnoreProperties(ignoreUnknown = true)
		public record TossPayment(
			String paymentKey,
			String orderName,
			String method,
			Integer totalAmount,
			@JsonProperty("approvedAt")
			String approveDateTime
		) {}
	}
}
