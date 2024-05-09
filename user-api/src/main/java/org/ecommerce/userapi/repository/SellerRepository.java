package org.ecommerce.userapi.repository;

import org.ecommerce.userapi.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SellerRepository extends JpaRepository<Seller, Integer>, SellerCustomRepository {
}
