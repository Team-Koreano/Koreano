package org.ecommerce.userapi.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.ecommerce.userapi.entity.enumerated.Gender;
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
@Table(name = "users", indexes = @Index(name = "idx_users_email", columnList = "email", unique = true))
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Users {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String password;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Gender gender;

	@Column(nullable = false)
	private Short age;

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
	@Enumerated(EnumType.STRING)
	private UserStatus userStatus = UserStatus.GENERAL;

	@OneToMany(mappedBy = "users", cascade = CascadeType.ALL)
	private List<UsersAccount> usersAccounts = new ArrayList<>();

	@OneToMany(mappedBy = "users", cascade = CascadeType.ALL)
	private List<Address> addresses = new ArrayList<>();

	public static Users ofRegister(String email, String name, String password, Gender gender, Short age,
		String phoneNumber) {
		Users users = new Users();
		users.email = email;
		users.name = name;
		users.password = password;
		users.age = age;
		users.gender = gender;
		users.phoneNumber = phoneNumber;
		return users;
	}

	public boolean isValidStatus() {
		return this.userStatus == UserStatus.GENERAL &&
			!this.isDeleted;
	}

	public boolean isValidUser(String email, String phoneNumber) {
		return Objects.equals(email, this.email) &&
			Objects.equals(phoneNumber, this.phoneNumber) &&
			isValidStatus();
	}

	public void withdrawal() {
		this.userStatus = UserStatus.WITHDRAWAL;
		this.isDeleted = true;
	}
}
