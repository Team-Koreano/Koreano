package org.ecommerce.orderapi.entity;

import static org.ecommerce.orderapi.entity.enumerated.OrderStatus.*;
import static org.ecommerce.orderapi.entity.enumerated.OrderStatusReason.*;
import static org.ecommerce.orderapi.util.OrderPolicyConstants.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.ecommerce.orderapi.entity.enumerated.OrderStatus;
import org.ecommerce.orderapi.entity.enumerated.OrderStatusReason;
import org.hibernate.annotations.CreationTimestamp;

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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_item")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class OrderItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	private Order order;

	@Column(nullable = false)
	private Integer productId;

	@Column(nullable = false)
	private String productName;

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
	private Integer sellerId;

	@Column(nullable = false)
	private String sellerName;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private OrderStatus status = OPEN;

	@Column
	@Enumerated(EnumType.STRING)
	private OrderStatusReason statusReason;

	@Column
	@CreationTimestamp
	private LocalDateTime statusDatetime;

	@OneToMany(mappedBy = "orderItem", cascade = CascadeType.ALL)
	private List<OrderStatusHistory> orderStatusHistories = new ArrayList<>();

	static OrderItem ofAdd(
			final Order order,
			final Integer productId,
			final String productName,
			final Integer price,
			final Integer quantity,
			final Integer deliveryFee,
			final Integer sellerId,
			final String sellerName
	) {
		final OrderItem orderItem = new OrderItem();
		orderItem.order = order;
		orderItem.productId = productId;
		orderItem.productName = productName;
		orderItem.price = price;
		orderItem.quantity = quantity;
		orderItem.totalPrice = price * quantity;
		orderItem.deliveryFee = deliveryFee;
		orderItem.paymentAmount = price * quantity + deliveryFee;
		orderItem.sellerId = sellerId;
		orderItem.sellerName = sellerName;
		orderItem.orderStatusHistories = new ArrayList<>();
		orderItem.orderStatusHistories.add(OrderStatusHistory.ofRecord(orderItem, OPEN));
		return orderItem;
	}

	void completedOrderItem() {
		changeStatus(CLOSED, null);
		orderStatusHistories.add(
				OrderStatusHistory.ofRecord(this, CLOSED));
	}

	void approve() {
		changeStatus(APPROVE, COMPLETE_PAYMENT);
		orderStatusHistories.add(
				OrderStatusHistory.ofRecord(this, APPROVE));
	}

	void cancel() {
		changeStatus(CANCELLED, REFUND);
		orderStatusHistories.add(
				OrderStatusHistory.ofRecord(this, CANCELLED));
	}

	boolean isCancelableStatus() {
		return this.status == CLOSED;
	}

	boolean isCancellableOrderDate() {
		final LocalDateTime now = LocalDateTime.now();
		final Duration duration = Duration.between(this.statusDatetime, now);
		return duration.toDays() <= ORDER_CANCELLABLE_DATE;
	}

	boolean isRefundedOrderStatus() {
		return status == CANCELLED;
	}

	boolean isRefundedStatusReason() {
		return statusReason == REFUND;
	}

	boolean isCompletedOrderItem() {
		return this.status == CLOSED;
	}

	private void changeStatus(
			final OrderStatus changeStatus,
			final OrderStatusReason changeStatusReason
	) {
		status = changeStatus;
		statusReason = changeStatusReason;
		statusDatetime = LocalDateTime.now();
	}
}