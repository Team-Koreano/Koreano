package org.ecommerce.userapi.dto;

import org.ecommerce.userapi.entity.Users;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
	UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
	UserDto toDto(Users seller);
	UserDto fromAccessToken(String accessToken);
}
