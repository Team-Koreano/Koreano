package org.ecommerce.userapi.repository.impl;

import static org.ecommerce.userapi.entity.QUsers.*;

import java.util.Optional;

import org.ecommerce.userapi.entity.Users;
import org.ecommerce.userapi.repository.UserCustomRepository;
import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserCustomRepository {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public Optional<Users> findUsersById(Integer id) {
		return Optional.ofNullable(jpaQueryFactory.selectFrom(users)
			.where(users.id.eq(id))
			.fetchFirst());
	}
}
