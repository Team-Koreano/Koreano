package org.ecommerce.userapi.repository.impl;

import static org.ecommerce.userapi.entity.QAddress.*;

import java.util.List;

import org.ecommerce.userapi.entity.Address;
import org.ecommerce.userapi.repository.AddressCustomRepository;
import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AddressRepositoryImpl implements AddressCustomRepository {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public List<Address> findByUsersIdAndIsDeletedIsFalse(final Integer userId) {
		return jpaQueryFactory.selectFrom(address)
			.where(address.users.id.eq(userId)
				.and(address.isDeleted.isFalse()))
			.fetch();
	}
}
