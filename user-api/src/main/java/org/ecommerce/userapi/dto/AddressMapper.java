package org.ecommerce.userapi.dto;

import org.ecommerce.userapi.entity.Address;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AddressMapper {

	AddressMapper INSTANCE = Mappers.getMapper(AddressMapper.class);

	AddressDto addressToDto(Address address);

	AddressDto.Response.Register addressDtoToResponse(AddressDto addressDto);

}
