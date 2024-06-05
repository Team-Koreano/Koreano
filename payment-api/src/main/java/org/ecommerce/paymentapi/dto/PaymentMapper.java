package org.ecommerce.paymentapi.dto;

import org.ecommerce.paymentapi.dto.response.PaymentDetailResponse;
import org.ecommerce.paymentapi.dto.response.PaymentWithDetailResponse;
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
	@Mapping(source = "paymentDetails", target = "paymentDetailDtos")
	@Mapping(source = "status", target = "processStatus")
	PaymentDtoWithDetail toPaymentWithDetailDto(Payment payment);

	PaymentWithDetailResponse toPaymentWithDetailResponse(PaymentDtoWithDetail paymentDto);

	@Mapping(source = "userBeanPay.userId", target = "userId")
	@Mapping(source = "sellerBeanPay.userId", target = "sellerId")
	@Mapping(source = "chargeInfo.paymentKey", target = "paymentKey")
	@Mapping(source = "chargeInfo.payType", target = "payType")
	@Mapping(source = "chargeInfo.approveDateTime", target = "approveDateTime")
	PaymentDetailDto toPaymentDetailDto(PaymentDetail beanPayDetail);

	PaymentDetailResponse toPaymentDetailResponse(PaymentDetailDto beanPayDetailDto);
}