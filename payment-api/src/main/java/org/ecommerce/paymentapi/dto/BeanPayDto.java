package org.ecommerce.paymentapi.dto;

import java.time.LocalDateTime;

import org.antlr.v4.runtime.misc.NotNull;
import org.ecommerce.paymentapi.entity.BeanPay;
import org.ecommerce.paymentapi.entity.type.BeanPayStatus;
import org.ecommerce.paymentapi.entity.type.ProcessStatus;
import org.springframework.validation.annotation.Validated;

public class BeanPayDto {
	public static class Request {
		public record PreCharge(
			Integer userId,
			Integer amount
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
	}
}
