package org.ecommerce.userapi.repository;

import java.util.Optional;

import org.ecommerce.userapi.entity.Users;

public interface UserCustomRepository {

	Optional<Users> findUsersByEmailAndPhoneNumber(final String email, final String phoneNumber);

	boolean existsByEmailOrPhoneNumber(String email, String phoneNumber);
}
