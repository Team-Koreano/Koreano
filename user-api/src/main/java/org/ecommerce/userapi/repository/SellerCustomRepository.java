package org.ecommerce.userapi.repository;

import org.ecommerce.userapi.entity.Seller;

public interface SellerCustomRepository {

	boolean existsByEmailOrPhoneNumber(final String email, final String phoneNumber);

	Seller findSellerByEmailAndIsDeletedIsFalse(final String email);

	Seller findSellerByIdAndIsDeletedIsFalse(final Integer sellerId);
}
