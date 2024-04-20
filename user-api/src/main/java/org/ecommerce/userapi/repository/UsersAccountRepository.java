package org.ecommerce.userapi.repository;

import org.ecommerce.userapi.entity.UsersAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersAccountRepository extends JpaRepository<UsersAccount,Integer> {
}
