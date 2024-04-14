package org.ecommerce.userapi.entity;

import java.time.LocalDateTime;

import org.ecommerce.userapi.entity.type.Gender;
import org.ecommerce.userapi.entity.type.UserStatus;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Users implements Member {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(nullable = false)
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

	@Column(nullable = false)
	private String phoneNumber;

	@CreationTimestamp
	@Column(updatable = false)
	private LocalDateTime createDatetime;

	@Column()
	private boolean isDeleted;

	@UpdateTimestamp
	@Column()
	private LocalDateTime updateDatetime;

	@ColumnDefault("0")
	@Column()
	private Integer beanPay;

	@ColumnDefault("0")
	@Column(name = "status")
	private UserStatus userStatus;

	public static Users ofRegister(String email, String name, String password, Gender gender, Short age,
		String phoneNumber) {
		Users users = new Users();
		users.email = email;
		users.name = name;
		users.password = password;
		users.age = age;
		users.beanPay = 0;
		users.gender = gender;
		users.isDeleted = false;
		users.phoneNumber = phoneNumber;
		users.userStatus = UserStatus.GENERAL;
		return users;
	}
}
