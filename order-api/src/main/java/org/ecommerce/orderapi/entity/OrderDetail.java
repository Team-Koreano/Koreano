package org.ecommerce.orderapi.entity;

import org.ecommerce.orderapi.entity.type.OrderStatus;
import org.ecommerce.orderapi.entity.type.OrderStatusReason;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Table(name = "order_detail")
@Getter
public class OrderDetail {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	private Order order;

	@Column(nullable = false)
	private Integer productId;

	@Column(nullable = false)
	private Integer price;

	@Column(nullable = false)
	private Short quantity;

	@Column(nullable = false)
	private Integer totalPrice;

	@Column(nullable = false)
	private Integer deliveryFee;

	@Column(nullable = false)
	private Integer paymentAmount;

	@Column(nullable = false)
	private String seller;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private OrderStatus status;

	@Column
	@Enumerated(EnumType.STRING)
	private OrderStatusReason statusReason;
}
