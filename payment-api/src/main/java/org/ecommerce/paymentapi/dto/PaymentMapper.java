package org.ecommerce.paymentapi.dto;

import org.ecommerce.paymentapi.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PaymentMapper {

	PaymentMapper INSTANCE = Mappers.getMapper(PaymentMapper.class);

	@Mapping(source = "userBeanPay.userId", target = "userId")
	@Mapping(source = "status", target = "processStatus")
	PaymentDto toDto(Payment payment);

}