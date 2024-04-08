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
	@JoinColumn(name = "order_id", nullable = false)
	private Order order;

	@Column(name = "product_id", nullable = false)
	private Integer productId;

	@Column(name = "price", nullable = false)
	private Integer price;

	@Column(name = "quantity", nullable = false)
	private Short quantity;

	@Column(name = "total_price", nullable = false)
	private Integer totalPrice;

	@Column(name = "delivery_fee", nullable = false)
	private Integer deliveryFee;

	@Column(name = "payment_amount", nullable = false)
	private Integer paymentAmount;

	@Column(name = "seller", nullable = false)
	private String seller;

	@Column(name = "status", nullable = false)
	@Enumerated(EnumType.STRING)
	private OrderStatus status;

	@Column(name = "status_reason")
	@Enumerated(EnumType.STRING)
	private OrderStatusReason statusReason;
}
