package org.ecommerce.userapi.repository.impl;

import static org.ecommerce.userapi.entity.QUsersAccount.*;

import java.util.List;

import org.ecommerce.userapi.entity.UsersAccount;
import org.ecommerce.userapi.repository.UserAccountCustomRepository;
import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserAccountRepositoryImpl implements UserAccountCustomRepository {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public List<UsersAccount> findByUsersId(final Integer userId) {
		return jpaQueryFactory.selectFrom(usersAccount)
			.where(usersAccount.users.id.eq(userId))
			.fetch();
	}
}
