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
@Table(name = "address")
@Getter
public class Address {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "users_id")
	private Users users;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String postAddress;

	@Column(nullable = false)
	private String detail;

	@CreationTimestamp
	@Column(updatable = false)
	private LocalDateTime createDatetime;

	@Column()
	private boolean isDeleted = false;

	@UpdateTimestamp
	@Column()
	private LocalDateTime updateDatetime;

	public static Address ofRegister(Users users, String name, String postAddress, String detail) {
		Address address = new Address();
		address.users = users;
		address.name = name;
		address.postAddress = postAddress;
		address.detail = detail;
		users.getAddresses().add(address);
		return address;
	}

	public void delete() {
		this.isDeleted = true;
	}
}
