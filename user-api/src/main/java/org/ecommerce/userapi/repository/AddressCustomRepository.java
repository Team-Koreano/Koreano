package org.ecommerce.userapi.repository;

import java.util.List;

import org.ecommerce.userapi.entity.Address;

public interface AddressCustomRepository {
	List<Address> findByUsersIdAndIsDeletedIsFalse(final Integer userId);

}
