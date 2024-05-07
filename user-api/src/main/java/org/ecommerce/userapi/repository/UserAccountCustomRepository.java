package org.ecommerce.userapi.repository;

import java.util.List;

import org.ecommerce.userapi.entity.UsersAccount;

public interface UserAccountCustomRepository {
	List<UsersAccount> findByUsersId(final Integer userId);
}
