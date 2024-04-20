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

	@Column(nullable = false)
	private String number;

	@Column(nullable = false)
	private String bankName;

	@CreationTimestamp
	@Column(updatable = false)
	private LocalDateTime createDatetime;

	@Column()
	private boolean isDeleted;

	@UpdateTimestamp
	@Column()
	private LocalDateTime updateDatetime;

	public static SellerAccount ofRegister(Seller seller, String number, String bankName) {
		SellerAccount sellerAccount = new SellerAccount();
		sellerAccount.seller = seller;
		sellerAccount.bankName = bankName;
		sellerAccount.number = number;
		sellerAccount.isDeleted = false;
		return sellerAccount;
	}
}
