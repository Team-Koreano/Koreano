package org.ecommerce.orderapi.dto;

import org.ecommerce.orderapi.entity.Order;
import org.ecommerce.orderapi.entity.OrderDetail;
import org.ecommerce.orderapi.entity.OrderStatusHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderMapper {
	OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

	@Mapping(source = "order.orderDetails", target = "orderDetailDtos")
	OrderDto OrderToDto(Order order);

	@Mapping(source = "orderDto.orderDetailDtos", target = "orderDetailResponses")
	OrderDto.Response OrderDtoToResponse(OrderDto orderDto);

	OrderDetailDto orderDetailToDto(OrderDetail orderDetail);

	OrderDetailDto.Response orderDetailDtoToResponse(OrderDetailDto orderDetailDto);

	OrderStatusHistoryDto orderStatusHistoryToDto(
			OrderStatusHistory orderStatusHistory);

	OrderStatusHistoryDto.Response orderStatusHistoryDtotoResponse(
			OrderStatusHistoryDto orderStatusHistoryDto);
}
