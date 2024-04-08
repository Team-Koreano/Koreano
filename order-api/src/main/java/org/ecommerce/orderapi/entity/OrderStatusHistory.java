package org.ecommerce.orderapi.entity;

import java.time.LocalDateTime;

import org.ecommerce.orderapi.entity.type.OrderStatus;
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
import lombok.Getter;

@Entity
@Table(name = "order_status_history")
@Getter
public class OrderStatusHistory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "oder_detail_id", nullable = false)
	private OrderDetail orderDetail;

	@Column(name = "change_status", nullable = false)
	@Enumerated(EnumType.STRING)
	private OrderStatus changeStatus;

	@CreationTimestamp
	@Column(name = "status_change_datetime", nullable = false, updatable = false)
	private LocalDateTime statusChangeDatetime;
}
