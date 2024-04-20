package org.ecommerce.orderapi.dto;

import org.ecommerce.orderapi.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderMapper {
	OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

	OrderDto toDto(Order order);
	OrderDto.Response toResponse(OrderDto orderDto);
}
