package org.ecommerce.userapi.repository;

import org.ecommerce.userapi.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address,Integer> {
}
