package org.ecommerce.userapi.repository;

import java.util.Optional;

import org.ecommerce.userapi.entity.Users;

public interface UserCustomRepository {

	boolean existsByEmailOrPhoneNumber(final String email, final String phoneNumber);

	Optional<Users> findUsersByEmailAndIsDeletedIsFalse(final String email);

	Optional<Users> findUsersByIdAndIsDeletedIsFalse(final Integer userId);

}
