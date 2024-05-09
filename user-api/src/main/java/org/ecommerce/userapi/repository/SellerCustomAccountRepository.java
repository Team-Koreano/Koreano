package org.ecommerce.userapi.repository;

import java.util.List;

import org.ecommerce.userapi.entity.SellerAccount;

public interface SellerCustomAccountRepository {
	List<SellerAccount> findBySellerIdAndIsDeletedIsFalse(Integer sellerId);
}
