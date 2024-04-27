package org.ecommerce.userapi.entity;

import java.time.LocalDateTime;

import org.ecommerce.userapi.entity.type.UserStatus;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "seller",indexes = @Index(name = "idx_seller_email", columnList = "email",unique = true))
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Seller implements Member {
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

	@Column()
	private Long beanPayId;

	@Column(name = "status")
	private UserStatus userStatus = UserStatus.GENERAL;

	public static Seller ofRegister(String email, String name, String password, String address, String phoneNumber) {
		Seller seller = new Seller();
		seller.email = email;
		seller.name = name;
		seller.address = address;
		seller.password = password;
		seller.phoneNumber = phoneNumber;
		return seller;
	}
}
