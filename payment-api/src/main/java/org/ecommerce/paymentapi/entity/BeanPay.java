package org.ecommerce.paymentapi.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.ecommerce.paymentapi.entity.type.BeanPayStatus;
import org.ecommerce.paymentapi.entity.type.ProcessStatus;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "beanpay")
public class BeanPay {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 50)
	private String paymentKey;

	@Column(name = "user_id")
	private Integer userId;

	@ColumnDefault("0")
	@Column(name = "amount", nullable = false)
	private Integer amount;

	@Enumerated(EnumType.STRING)
	@Column(name = "beanpay_status", nullable = false)
	private BeanPayStatus beanPayStatus;

	@Enumerated(EnumType.STRING)
	@Column(name = "process_status", nullable = false)
	private ProcessStatus processStatus;

	@CreationTimestamp
	@Column(name = "create_datetime", updatable = false)
	private LocalDateTime createDateTime;

	public static BeanPay ofCreate(Integer userId, Integer amount) {
		BeanPay beanPay = new BeanPay();
		beanPay.userId = userId;
		beanPay.amount = amount;
		beanPay.beanPayStatus = BeanPayStatus.DEPOSIT;
		beanPay.processStatus = ProcessStatus.PENDING;
		beanPay.createDateTime = LocalDateTime.now();
		return beanPay;
	}

}
