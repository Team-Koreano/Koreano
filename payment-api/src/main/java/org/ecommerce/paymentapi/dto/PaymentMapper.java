package org.ecommerce.paymentapi.dto;

import org.ecommerce.paymentapi.entity.Payment;
import org.ecommerce.paymentapi.entity.PaymentDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PaymentMapper {

	PaymentMapper INSTANCE = Mappers.getMapper(PaymentMapper.class);

	@Mapping(source = "userBeanPay.userId", target = "userId")
	@Mapping(source = "status", target = "processStatus")
	PaymentDto paymentToDto(Payment payment);

	PaymentDto.Response paymentDtoToResponse(PaymentDto paymentDto);

	@Mapping(source = "userBeanPay.userId", target = "userId")
	@Mapping(source = "sellerBeanPay.userId", target = "sellerId")
	@Mapping(source = "chargeInfo.paymentKey", target = "paymentKey")
	@Mapping(source = "chargeInfo.payType", target = "payType")
	@Mapping(source = "chargeInfo.approveDateTime", target = "approveDateTime")
	PaymentDetailDto paymentDetailToDto(PaymentDetail beanPayDetail);

	PaymentDetailDto.Response paymentDetailDtoToResponse(PaymentDetailDto beanPayDetailDto);
}