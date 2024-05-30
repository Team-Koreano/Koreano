package org.ecommerce.orderapi.order.entity;

import static org.ecommerce.orderapi.order.util.OrderPolicyConstants.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.ecommerce.orderapi.order.entity.enumerated.OrderStatus;
import org.ecommerce.orderapi.order.entity.enumerated.OrderStatusReason;
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
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_item", indexes = @Index(name = "idx_seller_id", columnList = "sellerId"))
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

	@Column
	private Integer totalPrice = 0;

	@Column
	private Integer deliveryFee = 0;

	@Column
	private Integer paymentAmount = 0;

	@Column(nullable = false)
	private Integer sellerId;

	@Column(nullable = false)
	private String sellerName;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private OrderStatus status = OrderStatus.OPEN;

	@Column
	@Enumerated(EnumType.STRING)
	private OrderStatusReason statusReason;

	@Column
	@CreationTimestamp
	private LocalDateTime statusDatetime;

	@Column
	private LocalDateTime paymentDatetime;

	@OneToMany(mappedBy = "orderItem", cascade = CascadeType.ALL)
	private List<OrderStatusHistory> orderStatusHistories = new ArrayList<>();

	static OrderItem ofAdd(
			final Order order,
			final Integer productId,
			final String productName,
			final Integer price,
			final Integer quantity,
			final Integer sellerId,
			final String sellerName
	) {
		final OrderItem orderItem = new OrderItem();
		orderItem.order = order;
		orderItem.productId = productId;
		orderItem.productName = productName;
		orderItem.price = price;
		orderItem.quantity = quantity;
		orderItem.sellerId = sellerId;
		orderItem.sellerName = sellerName;
		orderItem.orderStatusHistories = new ArrayList<>();
		orderItem.orderStatusHistories.add(
				OrderStatusHistory.ofRecord(orderItem, OrderStatus.OPEN));
		return orderItem;
	}

	void completedOrderItem() {
		changeStatus(OrderStatus.CLOSED, null);
		orderStatusHistories.add(
				OrderStatusHistory.ofRecord(this, OrderStatus.CLOSED));
	}

	void approve(
			final Integer totalPrice,
			final Integer deliveryFee,
			final Integer paymentAmount,
			final LocalDateTime paymentDatetime
	) {
		this.totalPrice = totalPrice;
		this.deliveryFee = deliveryFee;
		this.paymentAmount = paymentAmount;
		this.paymentDatetime = paymentDatetime;
		changeStatus(OrderStatus.APPROVE, OrderStatusReason.COMPLETE_PAYMENT);
		orderStatusHistories.add(
				OrderStatusHistory.ofRecord(this, OrderStatus.APPROVE));
	}

	void cancel() {
		changeStatus(OrderStatus.CANCELLED, OrderStatusReason.REFUND);
		orderStatusHistories.add(
				OrderStatusHistory.ofRecord(this, OrderStatus.CANCELLED));
	}

	boolean isCancelableStatus() {
		return this.status == OrderStatus.CLOSED;
	}

	boolean isCancellableOrderDate() {
		final LocalDateTime now = LocalDateTime.now();
		final Duration duration = Duration.between(this.statusDatetime, now);
		return duration.toDays() <= ORDER_CANCELLABLE_DATE;
	}

	public boolean isRefundedOrderStatus() {
		return status == OrderStatus.CANCELLED;
	}

	public boolean isRefundedStatusReason() {
		return statusReason == OrderStatusReason.REFUND;
	}

	boolean isCompletedOrderItem() {
		return this.status == OrderStatus.CLOSED;
	}

	boolean isApprovableOrderItem() {
		return this.status == OrderStatus.OPEN;
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
