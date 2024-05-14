package org.ecommerce.userapi.repository.impl;

import org.ecommerce.userapi.repository.SellerCustomRepository;
import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SellerRepositoryImpl implements SellerCustomRepository {

	private final JPAQueryFactory jpaQueryFactory;

}
