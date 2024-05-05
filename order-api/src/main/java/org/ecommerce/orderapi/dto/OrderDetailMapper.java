package org.ecommerce.orderapi.dto;

import org.ecommerce.orderapi.entity.OrderDetail;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderDetailMapper {
	OrderDetailMapper INSTANCE = Mappers.getMapper(OrderDetailMapper.class);

	OrderDetailDto toOrderDetailDto(OrderDetail orderDetail);

	OrderDetailDto.Response toResponse(OrderDetailDto orderDetailDto);
}
