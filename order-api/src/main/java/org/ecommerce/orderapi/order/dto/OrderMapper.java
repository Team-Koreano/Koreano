package org.ecommerce.orderapi.order.dto;

import org.ecommerce.orderapi.order.dto.response.CreateOrderResponse;
import org.ecommerce.orderapi.order.dto.response.InquiryOrderItemResponse;
import org.ecommerce.orderapi.order.dto.response.InquiryOrderItemStatusHistoryResponse;
import org.ecommerce.orderapi.order.dto.response.OrderItemResponse;
import org.ecommerce.orderapi.order.dto.response.OrderResponse;
import org.ecommerce.orderapi.order.dto.response.OrderStatusHistoryResponse;
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

	OrderDto toDto(Order order);

	OrderResponse toResponse(OrderDto orderDto);

	OrderItemDto toDto(OrderItem orderItem);

	OrderItemResponse toResponse(OrderItemDto orderItemDto);

	OrderStatusHistoryDto toDto(OrderStatusHistory orderStatusHistory);

	OrderStatusHistoryResponse toResponse(
			OrderStatusHistoryDto orderStatusHistoryDto);

	@Mapping(source = "order.orderItems", target = "orderItemDtoList")
	OrderDtoWithOrderItemDtoList toOrderDtoWithOrderItemDtoList(Order order);

	@Mapping(source = "orderDtoWithOrderItemDtoList.orderItemDtoList", target = "orderItemResponses")
	CreateOrderResponse toCreateOrderResponse(
			OrderDtoWithOrderItemDtoList orderDtoWithOrderItemDtoList);

	@Mapping(source = "orderItem.order", target = "orderDto")
	OrderItemDtoWithOrderDto toOrderItemDtoWithOrderDto(OrderItem orderItem);

	@Mapping(source = "orderItemDtoWithOrderDto.orderDto", target = "orderResponse")
	InquiryOrderItemResponse toInquiryOrderItemResponse(
			OrderItemDtoWithOrderDto orderItemDtoWithOrderDto);

	@Mapping(source = "orderItem.orderStatusHistories", target = "orderStatusHistoryDtoList")
	OrderItemDtoWithOrderStatusHistoryDtoList toOrderItemStatusHistoryDto(
			OrderItem orderItem);

	@Mapping(source = "orderItemDtoWithOrderStatusHistoryDtoList.orderStatusHistoryDtoList", target = "orderStatusHistoryResponses")
	InquiryOrderItemStatusHistoryResponse toInquiryOrderItemStatusHistoryResponse(
			OrderItemDtoWithOrderStatusHistoryDtoList orderItemDtoWithOrderStatusHistoryDtoList
	);

}
