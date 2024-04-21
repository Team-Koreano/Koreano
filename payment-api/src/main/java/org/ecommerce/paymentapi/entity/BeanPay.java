package org.ecommerce.paymentapi.entity;

import static org.ecommerce.paymentapi.utils.BeanPayTimeFormatUtil.*;

import java.time.LocalDateTime;
import java.util.UUID;

import org.ecommerce.paymentapi.dto.TossDto;
import org.ecommerce.paymentapi.entity.type.BeanPayStatus;
import org.ecommerce.paymentapi.entity.type.ProcessStatus;
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



	public boolean validBeanPay(UUID orderId, Integer amount) {
		beginValidProcess();
		return this.getId().equals(orderId) &&
			this.getAmount().equals(amount);
	}

	public void chargeComplete(TossDto.Response.TossPayment response) {
		this.paymentKey = response.paymentKey();
		this.approveDateTime = stringToDateTime(response.approveDateTime());
		changeProcessStatus(ProcessStatus.COMPLETED);
	}

	public void chargeFail(String message) {
		this.cancelOrFailReason = message;
		changeProcessStatus(ProcessStatus.CANCELLED);
	}

	private void beginValidProcess() {
		this.processStatus = ProcessStatus.IN_PROGRESS;
	}

	private void changeProcessStatus(ProcessStatus status) {
		this.processStatus = status;
	}
}
