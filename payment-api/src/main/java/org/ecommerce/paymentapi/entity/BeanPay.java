package org.ecommerce.paymentapi.entity;

import java.time.LocalDateTime;

import org.ecommerce.paymentapi.entity.type.BeanPayStatus;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Entity
@Table(name = "beanpay")
public class BeanPay {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "user_id")
	private Integer userId;

	@ColumnDefault("0")
	@Column(name = "amount", nullable = false)
	private Integer amount;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private BeanPayStatus status;

	@CreationTimestamp
	@Column(name = "create_datetime", updatable = false)
	private LocalDateTime createDateTime;

}
