package org.ecommerce.userapi.repository.impl;

import org.ecommerce.userapi.repository.SellerCustomAccountRepository;
import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SellerAccountRepositoryImpl implements SellerCustomAccountRepository {

	private final JPAQueryFactory jpaQueryFactory;

}
