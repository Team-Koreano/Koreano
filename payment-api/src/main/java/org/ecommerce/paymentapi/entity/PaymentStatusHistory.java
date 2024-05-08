package org.ecommerce.paymentapi.entity;

import java.time.LocalDateTime;

import org.ecommerce.paymentapi.entity.enumerate.PaymentStatus;
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
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(name = "payment_status_history")
public class PaymentStatusHistory {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	private PaymentDetail paymentDetail;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private PaymentStatus paymentStatus;

	@CreationTimestamp
	@Column
	private LocalDateTime statusChangeDatetime;

	protected static PaymentStatusHistory ofRecord(
		final PaymentDetail paymentDetail
	) {
		final PaymentStatusHistory paymentStatusHistory = new PaymentStatusHistory();
		paymentStatusHistory.paymentDetail = paymentDetail;
		paymentStatusHistory.paymentStatus = paymentDetail.getStatus();
		return paymentStatusHistory;
	}
}
