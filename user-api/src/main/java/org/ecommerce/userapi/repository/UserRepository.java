package org.ecommerce.userapi.repository;

import java.util.Optional;

import org.ecommerce.userapi.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<Users, Integer>, UserCustomRepository {
	Optional<Users> findUsersByEmail(final String email);

}
