package org.ecommerce.userapi.repository.impl;

import org.ecommerce.userapi.repository.AddressCustomRepository;
import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AddressRepositoryImpl implements AddressCustomRepository {

	private final JPAQueryFactory jpaQueryFactory;

}
