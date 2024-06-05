package org.ecommerce.userapi.repository;

import org.ecommerce.userapi.entity.Users;

public interface UserCustomRepository {

	boolean existsByEmailOrPhoneNumber(final String email, final String phoneNumber);

	Users findUsersByEmailAndIsDeletedIsFalse(final String email);

	Users findUsersByIdAndIsDeletedIsFalse(final Integer userId);

}
