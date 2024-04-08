package org.ecommerce.orderapi.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Table(name = "order")
@Getter
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "userId", nullable = false)
	private Integer userId;

	@Column(name = "receive_name", nullable = false)
	private String receiveName;

	@Column(name = "phone_number", nullable = false)
	private String phoneNumber;

	@Column(name = "address1", nullable = false)
	private String address1;

	@Column(name = "address2", nullable = false)
	private String address2;

	@Column(name = "delivery_comment")
	private String deliveryComment;

	@Column(name = "cash")
	private Integer cash;

	@Column(name = "beanpay")
	private Integer beanpay;

	@Column(name = "installment")
	private Short installment;

	@Column(name = "card_number")
	private String cardNumber;

	@Column(name = "card_type")
	private String cardType;

	@Column(name = "pay_method")
	private String payMethod;

	@Column(name = "payment_datetime")
	private LocalDateTime paymentDatetime;

	@CreationTimestamp
	@Column(name = "order_datetime", nullable = false, updatable = false)
	private LocalDateTime orderDatetime;
}
