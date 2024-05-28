package org.ecommerce.orderapi.order.dto;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.ecommerce.orderapi.order.dto.response.PaymentDetailResponse;
import org.ecommerce.orderapi.order.dto.response.PaymentResponse;
import org.ecommerce.orderapi.order.entity.Payment;
import org.ecommerce.orderapi.order.entity.PaymentDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PaymentMapper {
	PaymentMapper INSTANCE = Mappers.getMapper(PaymentMapper.class);

	@Mapping(target = "paymentDetails", source = "response.paymentDetailResponses", qualifiedByName = "toPaymentDetailMap")
	Payment paymentResponseToEntity(PaymentResponse response);

	@Named("toPaymentDetailMap")
	default Map<Long, PaymentDetail> toPaymentDetailMap(
			final List<PaymentDetailResponse> paymentDetailResponses
	) {
		if (paymentDetailResponses == null) {
			return null;
		}
		return paymentDetailResponses.stream()
				.map(this::paymentDetailResponseToEntity)
				.collect(Collectors.toMap(
						PaymentDetail::getOrderItemId,
						PaymentDetail -> PaymentDetail
				));
	}

	PaymentDetail paymentDetailResponseToEntity(PaymentDetailResponse response);
}
