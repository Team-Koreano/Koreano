package org.ecommerce.userapi.repository;

import org.ecommerce.userapi.entity.UsersAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersAccountRepository extends JpaRepository<UsersAccount, Integer>, UserAccountCustomRepository {
}
