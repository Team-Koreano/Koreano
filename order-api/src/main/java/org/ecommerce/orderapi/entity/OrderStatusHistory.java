package org.ecommerce.orderapi.entity;

import java.time.LocalDateTime;

import org.ecommerce.orderapi.entity.enumerated.OrderStatus;
import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;

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
	@JsonBackReference
	private OrderDetail orderDetail;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private OrderStatus changeStatus;

	@CreationTimestamp
	@Column
	private LocalDateTime statusChangeDatetime;

	static OrderStatusHistory ofRecord(
			final OrderDetail orderDetail,
			final OrderStatus changeStatus
	) {
		final OrderStatusHistory orderStatusHistory = new OrderStatusHistory();
		orderStatusHistory.orderDetail = orderDetail;
		orderStatusHistory.changeStatus = changeStatus;
		return orderStatusHistory;
	}
}
