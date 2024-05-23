package org.ecommerce.orderapi.order.dto;

import org.ecommerce.orderapi.order.entity.Order;
import org.ecommerce.orderapi.order.entity.OrderItem;
import org.ecommerce.orderapi.order.entity.OrderStatusHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderMapper {
	OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

	@Mapping(source = "order.orderItems", target = "orderItemDtos")
	OrderDto toDto(Order order);

	@Mapping(source = "orderDto.orderItemDtos", target = "orderItemResponses")
	OrderDto.Response toResponse(OrderDto orderDto);

	OrderItemDto toDto(OrderItem orderItem);

	OrderItemDto.Response toResponse(OrderItemDto orderItemDto);

	OrderStatusHistoryDto toDto(OrderStatusHistory orderStatusHistory);

	OrderStatusHistoryDto.Response toResponse(
			OrderStatusHistoryDto orderStatusHistoryDto);

	@Mapping(source = "orderItem.orderStatusHistories", target = "orderStatusHistoryDtos")
	OrderItemStatusHistoryDto toOrderItemStatusHistoryDto(OrderItem orderItem);

	@Mapping(source = "orderItemStatusHistoryDto.orderStatusHistoryDtos", target = "orderStatusHistoryResponses")
	OrderItemStatusHistoryDto.Response toResponse(
			OrderItemStatusHistoryDto orderItemStatusHistoryDto
	);
}
