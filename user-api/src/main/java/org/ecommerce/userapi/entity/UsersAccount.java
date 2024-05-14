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
@Table(name = "users_account")
@Getter
public class UsersAccount {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "users_id")
	private Users users;

	@Column(nullable = false)
	private String number;

	@Column(nullable = false)
	private String bankName;

	@CreationTimestamp
	@Column(updatable = false)
	private LocalDateTime createDatetime;

	@Column()
	private boolean isDeleted = false;

	@UpdateTimestamp
	@Column()
	private LocalDateTime updateDatetime;

	public static UsersAccount ofRegister(Users users, String number, String bankName) {
		UsersAccount usersAccount = new UsersAccount();
		usersAccount.users = users;
		usersAccount.bankName = bankName;
		usersAccount.number = number;
		users.getUsersAccounts().add(usersAccount);
		return usersAccount;
	}

	public void delete() {
		this.isDeleted = true;
	}
}
