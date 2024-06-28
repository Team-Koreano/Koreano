package org.ecommerce.productapi.repository;

import org.ecommerce.productapi.entity.SellerRep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SellerRepository extends JpaRepository<SellerRep, Integer> {
}
