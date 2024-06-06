package org.ecommerce.userapi.dto;

import org.ecommerce.userapi.dto.response.CreateAccountResponse;
import org.ecommerce.userapi.entity.SellerAccount;
import org.ecommerce.userapi.entity.UsersAccount;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountMapper {
	AccountMapper INSTANCE = Mappers.getMapper(AccountMapper.class);

	AccountDto toDto(SellerAccount sellerAccount);

	AccountDto toDto(UsersAccount usersAccount);

	CreateAccountResponse toResponse(AccountDto accountDto);

}
