package org.ecommerce.orderapi.repository.impl;

import org.ecommerce.orderapi.repository.OrderCustomRepository;
import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class OrderRepositoryImpl implements OrderCustomRepository {

	private final JPAQueryFactory jpaQueryFactory;

}
