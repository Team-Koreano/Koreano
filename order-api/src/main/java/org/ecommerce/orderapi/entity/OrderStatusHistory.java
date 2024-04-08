package org.ecommerce.orderapi.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
	private String changeStatus;

	@Column(name = "status_change_datetime", nullable = false)
	private LocalDateTime statusChangeDatetime;
}
