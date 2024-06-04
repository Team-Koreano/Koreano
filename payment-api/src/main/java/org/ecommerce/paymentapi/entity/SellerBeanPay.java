package org.ecommerce.paymentapi.entity;

import static org.ecommerce.paymentapi.exception.BeanPayErrorCode.*;

import java.time.LocalDateTime;

import org.ecommerce.common.error.CustomException;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "seller_beanpay")
public class SellerBeanPay {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(nullable = false, unique = true)
	private Integer sellerId;

	private Integer amount = 0;

	@CreationTimestamp
	@Column(updatable = false)
	private LocalDateTime createDateTime;

	private LocalDateTime deleteDateTime;

	public static SellerBeanPay ofCreate(
		Integer userId
	) {
		SellerBeanPay userBeanPay = new SellerBeanPay();
		userBeanPay.sellerId = userId;
		return userBeanPay;
	}

	protected void increaseBeanPay(Integer amount) {
		this.amount += amount;
	}

	protected void decreaseBeanPay(Integer amount) {
		int remainAmount = this.amount - amount;
		if(remainAmount < 0)
			throw new CustomException(INSUFFICIENT_AMOUNT);
		this.amount -= amount;
	}

	public void delete() {
		if(this.deleteDateTime != null)
			throw new CustomException(ALREADY_DELETE);
		if(this.amount > 0)
			throw new CustomException(REMAIN_BEANPAY);
		this.deleteDateTime = LocalDateTime.now();
	}
}
