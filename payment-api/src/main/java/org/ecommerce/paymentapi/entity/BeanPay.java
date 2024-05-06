package org.ecommerce.paymentapi.entity;

import java.time.LocalDateTime;

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

	public static BeanPay ofCreate(
		Integer userId, Role role
	) {
		BeanPay beanPay = new BeanPay();
		beanPay.userId = userId;
		beanPay.role = role;
		return beanPay;
	}
	public BeanPayDetail preCharge(Integer amount) {
		return BeanPayDetail.ofCreate(this, this.userId, amount);
	}

	public void chargeBeanPayDetail(Integer amount) {
		this.amount += amount;
	}
}
