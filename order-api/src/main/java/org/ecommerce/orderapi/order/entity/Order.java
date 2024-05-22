package org.ecommerce.orderapi.order.entity;

import static org.ecommerce.orderapi.order.exception.OrderErrorCode.*;
import static org.ecommerce.orderapi.order.util.OrderPolicyConstants.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.orderapi.order.entity.enumerated.OrderStatus;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "orders", indexes = {
		@Index(name = "idx_order_datetime", columnList = "orderDatetime"),
		@Index(name = "idx_order_user_id", columnList = "userId")
})
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Integer userId;

	@Column(nullable = false)
	private String userName;

	@Column(nullable = false)
	private String receiveName;

	@Column(nullable = false)
	private String phoneNumber;

	@Column(nullable = false)
	private String address1;

	@Column(nullable = false)
	private String address2;

	@Column
	private String deliveryComment;

	@Column
	private Integer totalPaymentAmount = 0;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private OrderStatus status = OrderStatus.OPEN;

	@Column
	@CreationTimestamp
	private LocalDateTime statusDatetime;

	@Column
	private LocalDateTime paymentDatetime;

	@Column
	@CreationTimestamp
	private LocalDateTime orderDatetime;

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
	private List<OrderItem> orderItems = new ArrayList<>();

	public static Order ofCreate(
			final Integer userId,
			final String userName,
			final String receiveName,
			final String phoneNumber,
			final String address1,
			final String address2,
			final String deliveryComment,
			final List<Product> products,
			final Map<Integer, Integer> quantities
	) {
		validateOrderLimit(products.size());
		final Order order = new Order();
		order.userId = userId;
		order.userName = userName;
		order.receiveName = receiveName;
		order.phoneNumber = phoneNumber;
		order.address1 = address1;
		order.address2 = address2;
		order.deliveryComment = deliveryComment;
		addOrderItems(order, products, quantities);
		return order;
	}

	public Order cancelItem(final Long orderItemId) {
		OrderItem orderItem = getOrderItemByOrderItemId(orderItemId);
		validateCancelableOrderItem(orderItem);
		orderItem.cancel();
		return this;
	}

	public void complete(final Set<Long> OrderItemIds) {
		orderItems.stream()
				.filter(orderItem ->
						OrderItemIds.contains(orderItem.getId()))
				.forEach(OrderItem::completedOrderItem);
		if (isCompletedAllOrderItems()) {
			changeStatus(OrderStatus.CLOSED);
		}
	}

	public void approve(
			final Integer totalPaymentAmount,
			final LocalDateTime paymentDatetime,
			final Map<Long, PaymentDetail> paymentDetails

	) {
		if (!isApprovableOrderStatus()) {
			throw new CustomException(MUST_OPEN_ORDER_TO_APPROVE);
		}
		approveOrderItems(paymentDetails);
		this.totalPaymentAmount = totalPaymentAmount;
		this.paymentDatetime = paymentDatetime;
		changeStatus(OrderStatus.APPROVE);
	}

	public boolean isStockOperationProcessableOrder() {
		return status == OrderStatus.APPROVE || status == OrderStatus.CANCELLED;
	}

	private OrderItem getOrderItemByOrderItemId(final Long orderItemId) {
		return orderItems.stream()
				.filter(orderItem -> orderItem.getId().equals(orderItemId))
				.findFirst()
				.orElseThrow(() -> new CustomException(NOT_FOUND_ORDER_ITEM_ID));
	}

	private static void validateOrderLimit(final int productCount) {
		if (productCount > MAXIMUM_ORDER_ITEMS) {
			throw new CustomException(TOO_MANY_PRODUCTS_ON_ORDER);
		}

		if (productCount < MINIMUM_ORDER_ITEMS) {
			throw new CustomException(TOO_FEW_PRODUCTS_ON_ORDER);
		}
	}

	private static void addOrderItems(
			final Order order,
			final List<Product> products,
			final Map<Integer, Integer> quantities
	) {
		order.orderItems = new ArrayList<>();
		products.forEach(
				product -> {
					validateQuantity(quantities.get(product.getId()));
					order.orderItems.add(
							OrderItem.ofAdd(
									order,
									product.getId(),
									product.getName(),
									product.getPrice(),
									quantities.get(product.getId()),
									product.getSellerId(),
									product.getSellerName()
							)
					);
				}
		);
	}

	private static void validateQuantity(final Integer quantity) {
		if (quantity > MAXIMUM_PRODUCT_QUANTITY) {
			throw new CustomException(TOO_MANY_QUANTITY_ON_ORDER);
		}

		if (quantity < MINIMUM_PRODUCT_QUANTITY) {
			throw new CustomException(TOO_FEW_QUANTITY_ON_ORDER);
		}
	}

	private void validateCancelableOrderItem(final OrderItem orderItem) {
		if (!orderItem.isCancelableStatus()) {
			throw new CustomException(MUST_CLOSED_ORDER_TO_CANCEL);
		}

		if (!orderItem.isCancellableOrderDate()) {
			throw new CustomException(TOO_OLD_ORDER_TO_CANCEL);
		}
	}

	private boolean isCompletedAllOrderItems() {
		return orderItems.stream().allMatch(OrderItem::isCompletedOrderItem);
	}

	private boolean isApprovableOrderStatus() {
		return status == OrderStatus.OPEN;
	}

	private void changeStatus(final OrderStatus changeStatus) {
		status = changeStatus;
		statusDatetime = LocalDateTime.now();
	}

	private void approveOrderItems(final Map<Long, PaymentDetail> paymentDetails) {
		orderItems.forEach(orderItem -> {
			if (!orderItem.isApprovableOrderItem()) {
				throw new CustomException(MUST_OPEN_ORDER_ITEM_TO_APPROVE);
			}
			PaymentDetail paymentDetail = paymentDetails.get(orderItem.getId());
			orderItem.approve(
					paymentDetail.getTotalPrice(),
					paymentDetail.getDeliveryFee(),
					paymentDetail.getPaymentAmount(),
					paymentDetail.getPaymentDatetime()
			);
		});
	}
}
