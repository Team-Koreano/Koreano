package org.ecommerce.paymentapi.entity;

import static org.ecommerce.paymentapi.utils.BeanPayTimeFormatUtil.*;

import java.time.LocalDateTime;
import java.util.UUID;

import org.ecommerce.paymentapi.dto.TossDto;
import org.ecommerce.paymentapi.entity.enumerate.BeanPayStatus;
import org.ecommerce.paymentapi.entity.enumerate.ProcessStatus;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "beanpay_detail")
public class BeanPayDetail {

	@Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(columnDefinition = "BINARY(16)")
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "beanpay_id")
	private BeanPay beanPay;

	@Column(length = 50)
	private String paymentKey;

	@Column(name = "user_id")
	private Integer userId;

	@Column(name = "amount", nullable = false)
	private Integer amount;

	@Column(name = "pay_type")
	private String payType;

	@Column
	private String cancelReason;

	@Column
	private String failReason;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private BeanPayStatus beanPayStatus = BeanPayStatus.DEPOSIT;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ProcessStatus processStatus = ProcessStatus.PENDING;

	@CreationTimestamp
	@Column(updatable = false)
	private LocalDateTime createDateTime = LocalDateTime.now();

	@Column(updatable = false)
	private LocalDateTime approveDateTime;

	protected static BeanPayDetail ofCreate(BeanPay beanPay, Integer userId,
		Integer amount) {
		BeanPayDetail beanPayDetail = new BeanPayDetail();
		beanPayDetail.beanPay = beanPay;
		beanPayDetail.userId = userId;
		beanPayDetail.amount = amount;
		return beanPayDetail;
	}

	protected static BeanPayDetail ofPayment(BeanPay beanPay, Integer userId,
		Integer amount) {
		BeanPayDetail beanPayDetail = ofCreate(beanPay, userId, amount);
		beanPayDetail.beanPayStatus = BeanPayStatus.PAYMENT;
		beanPayDetail.processStatus = ProcessStatus.COMPLETED;
		return beanPayDetail;
	}

	protected static BeanPayDetail ofReceive(BeanPay beanPay, Integer userId,
		Integer amount) {
		BeanPayDetail beanPayDetail = ofCreate(beanPay, userId, amount);
		beanPayDetail.beanPayStatus = BeanPayStatus.RECEIVE;
		beanPayDetail.processStatus = ProcessStatus.COMPLETED;
		return beanPayDetail;
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
		this.beanPay.chargeBeanPayDetail(this.amount);
	}

	public void chargeFail(String message) {
		this.failReason = message;
		changeProcessStatus(ProcessStatus.FAILED);
	}

	public void chargeCancel(String message) {
		this.cancelReason = message;
		changeProcessStatus(ProcessStatus.CANCELLED);
	}

	private void beginValidProcess() {
		this.processStatus = ProcessStatus.IN_PROGRESS;
	}

	private void changeProcessStatus(ProcessStatus status) {
		this.processStatus = status;
	}
}
