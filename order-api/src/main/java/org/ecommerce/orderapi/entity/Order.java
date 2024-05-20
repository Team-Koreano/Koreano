package org.ecommerce.orderapi.entity;

import static org.ecommerce.orderapi.entity.enumerated.OrderStatus.*;
import static org.ecommerce.orderapi.exception.OrderErrorCode.*;
import static org.ecommerce.orderapi.util.OrderPolicyConstants.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.orderapi.entity.enumerated.OrderStatus;
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
import lombok.Getter;

@Entity
@Table(name = "orders", indexes = {
		@Index(name = "idx_order_datetime", columnList = "orderDatetime"),
		@Index(name = "idx_order_user_id", columnList = "userId")
})
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
	private OrderStatus status;

	@Column
	@CreationTimestamp
	private LocalDateTime statusDatetime;

	@Column
	private LocalDateTime paymentDatetime;

	@CreationTimestamp
	@Column
	private LocalDateTime orderDatetime;

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
	private List<OrderItem> orderItems = new ArrayList<>();

	public static Order of(
			final Integer userId,
			final String userName,
			final String receiveName,
			final String phoneNumber,
			final String address1,
			final String address2,
			final String deliveryComment
	) {
		final Order order = new Order();
		order.userId = userId;
		order.userName = userName;
		order.receiveName = receiveName;
		order.phoneNumber = phoneNumber;
		order.address1 = address1;
		order.address2 = address2;
		order.deliveryComment = deliveryComment;
		return order;
	}

	public void place(
			final List<Product> products,
			final Map<Integer, Integer> quantities
	) {
		validateOrder(products.size());
		addOrderItems(products, quantities);
		changeStatus(OPEN);
	}

	public void cancelItem(final Long orderItemId) {
		OrderItem orderItem = getOrderItemByOrderItemId(orderItemId);
		isCancelableOrderItem(orderItem);
		orderItem.cancel();
	}

	public void complete(final Set<Long> successfulDecreaseStockOrderItemIds) {
		orderItems.stream()
				.filter(orderItem ->
						successfulDecreaseStockOrderItemIds.contains(orderItem.getId()))
				.forEach(OrderItem::completedOrderItem);
		if (isCompletedAllOrderItems()) {
			changeStatus(CLOSED);
		}
	}

	public void approve() {
		orderItems.forEach(OrderItem::approve);
		changeStatus(APPROVE);
	}

	public boolean isStockOperationProcessableOrder() {
		return status == APPROVE || status == CANCELLED;
	}

	public OrderItem getOrderItemByOrderItemId(final Long orderItemId) {
		return orderItems.stream()
				.filter(orderItem -> orderItem.getId().equals(orderItemId))
				.findFirst()
				.orElseThrow(() -> new CustomException(NOT_FOUND_ORDER_ITEM_ID));
	}

	private void validateOrder(final int productCount) {
		validateInitialOrder();
		validateInitialOrderItems();
		validateOrderLimit(productCount);
	}

	private void validateInitialOrder() {
		if (status != null || id != null) {
			throw new CustomException(NOT_CORRECT_STATUS_TO_PLACE);
		}
	}

	private void validateInitialOrderItems() {
		if (!orderItems.isEmpty()) {
			throw new CustomException(NOT_CORRECT_STATUS_TO_ADD);
		}
	}

	private void validateOrderLimit(final int productCount) {
		if (productCount > MAXIMUM_ORDER_ITEMS) {
			throw new CustomException(TOO_MANY_PRODUCTS_ON_ORDER);
		}

		if (productCount < MINIMUM_ORDER_ITEMS) {
			throw new CustomException(TOO_FEW_PRODUCTS_ON_ORDER);
		}
	}

	private void addOrderItems(
			final List<Product> products,
			final Map<Integer, Integer> quantities
	) {

		// TODO : 배송비 우선 무료로 고정, 추후 seller에서 정책 설정
		Integer DELIVERY_FEE = 0;
		orderItems = new ArrayList<>();
		products.forEach(
				product -> {
					validateQuantity(quantities.get(product.getId()));
					OrderItem orderItem = OrderItem.ofAdd(
							this,
							product.getId(),
							product.getName(),
							product.getPrice(),
							quantities.get(product.getId()),
							DELIVERY_FEE,
							product.getSellerId(),
							product.getSellerName()
					);
					orderItems.add(orderItem);
					totalPaymentAmount += orderItem.getPaymentAmount();
				}
		);
	}

	private void validateQuantity(final Integer quantity) {
		if (quantity > MAXIMUM_PRODUCT_QUANTITY) {
			throw new CustomException(TOO_MANY_QUANTITY_ON_ORDER);
		}

		if (quantity < MINIMUM_PRODUCT_QUANTITY) {
			throw new CustomException(TOO_FEW_QUANTITY_ON_ORDER);
		}
	}

	private void isCancelableOrderItem(final OrderItem orderItem) {
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

	private void changeStatus(final OrderStatus changeStatus) {
		status = changeStatus;
		statusDatetime = LocalDateTime.now();
	}

}
