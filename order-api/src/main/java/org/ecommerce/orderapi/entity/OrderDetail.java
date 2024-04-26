package org.ecommerce.orderapi.entity;

import java.util.ArrayList;
import java.util.List;

import org.ecommerce.orderapi.entity.type.OrderStatus;
import org.ecommerce.orderapi.entity.type.OrderStatusReason;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
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
	private Integer quantity;

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
	private OrderStatus status = OrderStatus.OPEN;

	@Column
	@Enumerated(EnumType.STRING)
	private OrderStatusReason statusReason;

	@OneToMany(mappedBy = "orderDetail", cascade = CascadeType.ALL)
	private List<OrderStatusHistory> orderStatusHistories = new ArrayList<>();

	public static OrderDetail ofPlace(
			final Order order,
			final Integer productId,
			final Integer price,
			final Integer quantity,
			final Integer deliveryFee,
			final String seller
	) {
		final OrderDetail orderDetail = new OrderDetail();
		orderDetail.order = order;
		orderDetail.productId = productId;
		orderDetail.price = price;
		orderDetail.quantity = quantity;
		orderDetail.totalPrice = price * quantity;
		orderDetail.deliveryFee = deliveryFee;
		orderDetail.paymentAmount = price * quantity + deliveryFee;
		orderDetail.seller = seller;
		return orderDetail;
	}

	public void recordOrderStatusHistory(final OrderStatusHistory orderStatusHistory) {
		orderStatusHistories.add(orderStatusHistory);
	}

}
