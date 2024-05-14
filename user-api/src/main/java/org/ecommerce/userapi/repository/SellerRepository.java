package org.ecommerce.userapi.repository;

import java.util.Optional;

import org.ecommerce.userapi.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface SellerRepository extends JpaRepository<Seller, Integer>, SellerCustomRepository {
	@Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
	boolean existsByEmailOrPhoneNumber(final String email, final String phoneNumber);

	Optional<Seller> findByEmail(final String email);
}
