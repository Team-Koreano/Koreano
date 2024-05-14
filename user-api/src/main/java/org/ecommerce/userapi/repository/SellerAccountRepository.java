package org.ecommerce.userapi.repository;

import org.ecommerce.userapi.entity.SellerAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerAccountRepository extends JpaRepository<SellerAccount, Integer>, SellerCustomAccountRepository {
}
