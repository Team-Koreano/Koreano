package org.ecommerce.paymentapi.dto;

import org.ecommerce.paymentapi.entity.PaymentDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PaymentDetailMapper {

	PaymentDetailMapper INSTANCE = Mappers.getMapper(PaymentDetailMapper.class);

	@Mapping(source = "userBeanPay.userId", target = "userId")
	@Mapping(source = "sellerBeanPay.userId", target = "sellerId")
	@Mapping(source = "chargeInfo.paymentKey", target = "paymentKey")
	@Mapping(source = "chargeInfo.payType", target = "payType")
	@Mapping(source = "chargeInfo.approveDateTime", target = "approveDateTime")
	PaymentDetailDto toDto(PaymentDetail beanPayDetail);

}