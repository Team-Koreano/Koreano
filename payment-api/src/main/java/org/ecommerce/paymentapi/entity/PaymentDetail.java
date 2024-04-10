package org.ecommerce.paymentapi.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "payment_detail")
public class PaymentDetail {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "payment_id", nullable = false)
	private Payment payment;

	@Column(name = "user_id")
	private Integer userId;

	@Column(name = "seller_id")
	private Integer sellerId;

	@ColumnDefault("0")
	@Column(name = "total_amount", nullable = false)
	private Integer totalAmount;

	@Column(name = "order_name", nullable = false)
	private String orderName;

	@CreationTimestamp
	@Column(name = "create_datetime", updatable = false)
	private LocalDateTime createDateTime;

	@UpdateTimestamp
	@Column(name = "update_datetime", insertable = false)
	private LocalDateTime updateDateTime;

	@Column(name = "is_deleted")
	private Boolean isDeleted;
}
