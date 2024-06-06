package org.ecommerce.userapi.dto;

import org.ecommerce.userapi.dto.response.CreateSellerResponse;
import org.ecommerce.userapi.entity.Seller;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SellerMapper {
	SellerMapper INSTANCE = Mappers.getMapper(SellerMapper.class);

	SellerDto toDto(Seller users);

	SellerDto toDto(String accessToken);

	CreateSellerResponse toResponse(SellerDto sellerDto);
}
