package org.ecommerce.orderapi.dto;

import org.ecommerce.orderapi.entity.Order;
import org.ecommerce.orderapi.entity.OrderItem;
import org.ecommerce.orderapi.entity.OrderStatusHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderMapper {
	OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

	@Mapping(source = "order.orderItems", target = "orderItemDtos")
	OrderDto OrderToDto(Order order);

	@Mapping(source = "orderDto.orderItemDtos", target = "orderItemResponses")
	OrderDto.Response OrderDtoToResponse(OrderDto orderDto);

	OrderItemDto orderItemToDto(OrderItem orderItem);

	OrderItemDto.Response orderItemDtoToResponse(OrderItemDto orderItemDto);

	OrderStatusHistoryDto orderStatusHistoryToDto(
			OrderStatusHistory orderStatusHistory);

	OrderStatusHistoryDto.Response orderStatusHistoryDtotoResponse(
			OrderStatusHistoryDto orderStatusHistoryDto);
}
