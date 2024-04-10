package org.ecommerce.userapi.repository;

import org.ecommerce.userapi.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<Users, Integer> {
		boolean existsByEmail(String email);
		boolean existsByPhoneNumber(String phoneNumber);
}
