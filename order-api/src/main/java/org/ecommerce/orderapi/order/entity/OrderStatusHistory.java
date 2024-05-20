package org.ecommerce.orderapi.order.entity;

import java.time.LocalDateTime;

import org.ecommerce.orderapi.order.entity.enumerated.OrderStatus;
import org.hibernate.annotations.CreationTimestamp;

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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_status_history")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class OrderStatusHistory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	private OrderItem orderItem;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private OrderStatus changeStatus;

	@CreationTimestamp
	@Column
	private LocalDateTime statusChangeDatetime;

	static OrderStatusHistory ofRecord(
			final OrderItem orderItem,
			final OrderStatus changeStatus
	) {
		final OrderStatusHistory orderStatusHistory = new OrderStatusHistory();
		orderStatusHistory.orderItem = orderItem;
		orderStatusHistory.changeStatus = changeStatus;
		return orderStatusHistory;
	}
}
