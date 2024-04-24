package org.ecommerce.userapi.repository;

import java.util.Optional;

import org.ecommerce.userapi.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SellerRepository extends JpaRepository<Seller, Integer> {
	boolean existsByEmailOrPhoneNumber(String email,String phoneNumber);

	Optional<Seller> findByEmail(String email);

	Optional<Seller> findById(Integer id);
}
