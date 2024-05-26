package org.ecommerce.orderapi.order.dto;

import org.ecommerce.orderapi.order.dto.response.ProductResponse;
import org.ecommerce.orderapi.order.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {

	ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

	Product responseToEntity(ProductResponse response);
}
