package org.ecommerce.userapi.repository;

import java.util.Optional;

import org.ecommerce.userapi.entity.Users;

public interface UserCustomRepository {
	Optional<Users> findUsersById(Integer id);
}
