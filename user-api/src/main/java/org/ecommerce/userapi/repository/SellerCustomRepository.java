package org.ecommerce.userapi.repository;

import java.util.Optional;

import org.ecommerce.userapi.entity.Seller;

public interface SellerCustomRepository {

	boolean existsByEmailOrPhoneNumber(final String email, final String phoneNumber);

	Optional<Seller> findSellerByEmailAndIsDeletedIsFalse(final String email);

	Optional<Seller> findSellerByIdAndIsDeletedIsFalse(final Integer sellerId);
}
