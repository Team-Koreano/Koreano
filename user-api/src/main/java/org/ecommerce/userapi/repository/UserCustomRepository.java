package org.ecommerce.userapi.repository;

import java.util.Optional;

import org.ecommerce.userapi.entity.Users;

public interface UserCustomRepository {

	boolean existsByEmailOrPhoneNumber(final String email, final String phoneNumber);

	Optional<Users> findUsersByEmail(final String email);
}
