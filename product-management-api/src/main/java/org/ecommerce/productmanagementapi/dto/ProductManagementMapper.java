package org.ecommerce.productmanagementapi.dto;

import org.ecommerce.product.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductManagementMapper {
	ProductManagementMapper INSTANCE = Mappers.getMapper(ProductManagementMapper.class);
	ProductManagementDto toDto(Product product);

	ProductManagementDto.Response toResponse(ProductManagementDto productManagementDto);
}
