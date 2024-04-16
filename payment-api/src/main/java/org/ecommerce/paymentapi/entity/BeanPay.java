package org.ecommerce.paymentapi.entity;

import static org.ecommerce.paymentapi.exception.BeanPayErrorCode.*;
import static org.ecommerce.paymentapi.utils.BeanPayTimeFormatUtil.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.ecommerce.common.error.ErrorCode;
import org.ecommerce.paymentapi.dto.BeanPayDto;
import org.ecommerce.paymentapi.entity.type.BeanPayStatus;
import org.ecommerce.paymentapi.entity.type.ProcessStatus;
import org.ecommerce.paymentapi.exception.BeanPayErrorCode;
import org.ecommerce.paymentapi.utils.BeanPayTimeFormatUtil;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "beanpay")
public class BeanPay {

	@Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(columnDefinition = "BINARY(16)")
	private UUID id;

	@Column(length = 50)
	private String paymentKey;

	@Column(name = "user_id")
	private Integer userId;

	@ColumnDefault("0")
	@Column(name = "amount", nullable = false)
	private Integer amount;

	@Column(name = "pay_type")
	private String payType;

	@Column
	private String cancelOrFailReason;

	@Enumerated(EnumType.STRING)
	@Column(name = "beanpay_status", nullable = false)
	private BeanPayStatus beanPayStatus;

	@Enumerated(EnumType.STRING)
	@Column(name = "process_status", nullable = false)
	private ProcessStatus processStatus;

	@CreationTimestamp
	@Column(name = "create_datetime", updatable = false)
	private LocalDateTime createDateTime;

	@Column(name = "approve_datetime", updatable = false)
	private LocalDateTime approveDateTime;

	public static BeanPay ofCreate(Integer userId, Integer amount) {
		BeanPay beanPay = new BeanPay();
		beanPay.userId = userId;
		beanPay.amount = amount;
		beanPay.beanPayStatus = BeanPayStatus.DEPOSIT;
		beanPay.processStatus = ProcessStatus.PENDING;
		beanPay.createDateTime = LocalDateTime.now();
		return beanPay;
	}

	public void inProgress() {
		this.processStatus = ProcessStatus.IN_PROGRESS;
	}

	public boolean validBeanPay(UUID orderId, Integer amount) {
		return this.getId().equals(orderId) &&
			this.getAmount().equals(amount);
	}

	public void complete(BeanPayDto.Response.TossPayment response) {
		this.processStatus = ProcessStatus.COMPLETED;
		this.paymentKey = response.paymentKey();
		this.approveDateTime = stringToDateTime(response.approveDateTime());
	}

	public void fail(ErrorCode errorCode) {
		this.cancelOrFailReason = errorCode.getMessage();
		this.processStatus = ProcessStatus.CANCELLED;
	}
}
