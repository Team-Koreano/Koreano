package org.ecommerce.paymentapi.entity;

import static org.ecommerce.paymentapi.exception.BeanPayErrorCode.*;

import java.time.LocalDateTime;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.paymentapi.entity.enumerate.Role;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(
	name = "beanpay",
	indexes = {
		@Index(name = "idx_userId", columnList = "userId"),
	}
)
public class BeanPay {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(nullable = false)
	private Integer userId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Role role;

	private Integer amount = 0;

	@CreationTimestamp
	@Column(updatable = false)
	private LocalDateTime createDateTime;

	private LocalDateTime deleteDateTime;

	public static BeanPay ofCreate(
		Integer userId, Role role
	) {
		BeanPay beanPay = new BeanPay();
		beanPay.userId = userId;
		beanPay.role = role;
		return beanPay;
	}

	public PaymentDetail beforeCharge(Integer amount) {
		return PaymentDetail.ofBeforeCharge(this, amount);
	}

	public void chargeBeanPayDetail(Integer amount) {
		increaseBeanPay(amount);
	}

	private void increaseBeanPay(Integer amount) {
		this.amount += amount;
	}

	private void decreaseBeanPay(Integer amount) {
		int remainAmount = this.amount - amount;
		if(remainAmount < 0)
			throw new CustomException(INSUFFICIENT_AMOUNT);
		this.amount -= amount;
	}

	public void payment(Integer amount, BeanPay sellerBeanPay) {
		this.decreaseBeanPay(amount);
		sellerBeanPay.increaseBeanPay(amount);
	}

	public void cancelPayment(Integer amount, BeanPay sellerBeanPay) {
		sellerBeanPay.decreaseBeanPay(amount);
		this.increaseBeanPay(amount);
	}

	public void delete() {
		if(this.deleteDateTime != null)
			throw new CustomException(ALREADY_DELETE);
		if(this.amount > 0)
			throw new CustomException(REMAIN_BEANPAY);
		this.deleteDateTime = LocalDateTime.now();
	}
}
