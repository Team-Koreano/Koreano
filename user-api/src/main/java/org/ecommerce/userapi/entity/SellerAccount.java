package org.ecommerce.userapi.entity;

import java.time.LocalDateTime;

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
import lombok.Getter;

@Entity
@Table(name = "seller_account")
@Getter
public class SellerAccount {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "seller_id")
	private Seller seller;

	@Column(name = "number",nullable = false)
	private Short number;

	@Column(name = "bank_name", nullable = false)
	private String bankName;

	@CreationTimestamp
	@Column(name = "create_datetime",updatable = false)
	private LocalDateTime createDatetime;

	@Column(name = "is_deleted")
	private Boolean isDeleted;

	@UpdateTimestamp
	@Column(name = "update_datetime",insertable = false)
	private LocalDateTime updateDatetime;
}
