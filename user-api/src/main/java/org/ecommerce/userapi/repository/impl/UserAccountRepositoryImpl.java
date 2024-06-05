package org.ecommerce.userapi.repository.impl;

import org.ecommerce.userapi.repository.UserAccountCustomRepository;
import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserAccountRepositoryImpl implements UserAccountCustomRepository {

	private final JPAQueryFactory jpaQueryFactory;
}
