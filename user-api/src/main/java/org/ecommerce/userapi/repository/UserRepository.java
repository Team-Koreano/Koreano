package org.ecommerce.userapi.repository;

import java.util.Optional;

import org.ecommerce.userapi.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserRepository extends JpaRepository<Users, Integer> {
	@Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
	boolean existsByEmailOrPhoneNumber(String email, String phoneNumber);

	Optional<Users> findUsersByEmail(String email);

}
