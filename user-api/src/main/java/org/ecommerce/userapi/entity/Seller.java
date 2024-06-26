package org.ecommerce.userapi.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.ecommerce.userapi.entity.enumerated.UserStatus;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "seller", indexes = @Index(name = "idx_seller_email", columnList = "email", unique = true))
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Seller {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String password;

	@Column()
	private String address;

	@Column(nullable = false, unique = true)
	private String phoneNumber;

	@CreationTimestamp
	@Column(updatable = false)
	private LocalDateTime createDatetime;

	@Column()
	private boolean isDeleted = false;

	@UpdateTimestamp
	@Column()
	private LocalDateTime updateDatetime;

	@Column(name = "status")
	@Enumerated(EnumType.STRING)
	private UserStatus userStatus = UserStatus.GENERAL;

	@OneToMany(mappedBy = "seller", cascade = CascadeType.ALL)
	private List<SellerAccount> sellerAccounts = new ArrayList<>();

	public static Seller ofRegister(String email, String name, String password, String address, String phoneNumber) {
		Seller seller = new Seller();
		seller.email = email;
		seller.name = name;
		seller.address = address;
		seller.password = password;
		seller.phoneNumber = phoneNumber;
		return seller;
	}

	public void withdrawal() {
		this.isDeleted = true;
		this.userStatus = UserStatus.WITHDRAWAL;
	}

	public boolean isValidSeller(String email, String phoneNumber) {
		return Objects.equals(email, this.email) &&
			Objects.equals(phoneNumber, this.phoneNumber) &&
			isValidStatus();

	}

	public boolean isValidStatus() {
		return this.userStatus == UserStatus.GENERAL &&
			!this.isDeleted;
	}
}
