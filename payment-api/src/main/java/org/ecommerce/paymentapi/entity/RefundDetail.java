package org.ecommerce.paymentapi.entity;

import java.time.LocalDateTime;

import org.ecommerce.paymentapi.entity.type.RefundStatus;
import org.hibernate.annotations.ColumnDefault;
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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Entity
@Table(name = "refund_detail")
public class RefundDetail {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "payment_id", nullable = false)
	private Payment payment;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "refund_id")
	private Refund refund;

	@ColumnDefault("0")
	@Column(name = "total_amount")
	private Integer totalAmount;

	@ColumnDefault("0")
	@Column(name = "delivery_fee")
	private Integer deliveryFee;

	@ColumnDefault("0")
	@Column(name = "amount")
	private Integer amount;

	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private RefundStatus status;

	@CreationTimestamp
	@Column(name = "create_datetime", updatable = false)
	private LocalDateTime createDateTime;
}
