package org.ecommerce.userapi.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
import jakarta.persistence.OneToMany;
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
	private Users usersId;

	@Column(name = "name",nullable = false)
	private String name;

	@Column(name = "password", nullable = false)
	private String password;


	@Column(name = "post_address", nullable = false)
	private String postAddress;

	@Column(name = "detail", nullable = false)
	private String detail;


	@CreationTimestamp
	@Column(name = "create_date")
	private LocalDateTime createDate;

	@Column(name = "is_deleted")
	private boolean isDeleted;

	@UpdateTimestamp
	@Column(name = "update_date")
	private LocalDateTime updateDate;
}
