package org.ecommerce.userapi.entity;

import java.time.LocalDateTime;

import org.ecommerce.userapi.entity.type.Gender;
import org.ecommerce.userapi.entity.type.Status;
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
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Users {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "email",nullable = false)
	private String email;

	@Column(name = "name",nullable = false)
	private String name;

	@Column(name = "password", nullable = false)
	private String password;

	@Enumerated(EnumType.STRING)
	@Column(name = "gender")
	private Gender gender;

	@Column(name = "age", nullable = false)
	private Short age;

	@Column(name = "phone_number", nullable = false)
	private String phoneNumber;

	@CreationTimestamp
	@Column(name = "create_datetime",updatable = false)
	private LocalDateTime createDatetime;

	@Column(name = "is_deleted")
	private Boolean isDeleted;

	@UpdateTimestamp
	@Column(name = "update_datetime")
	private LocalDateTime updateDatetime;

	@ColumnDefault("0")
	@Column(name = "beanpay")
	private Integer beanPay;

	@ColumnDefault("0")
	@Column(name = "status")
	private Status status;

}