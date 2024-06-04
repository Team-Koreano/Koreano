package org.ecommerce.paymentapi.dto;

import org.ecommerce.paymentapi.dto.response.SellerBeanPayResponse;
import org.ecommerce.paymentapi.entity.SellerBeanPay;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SellerBeanPayMapper {

	SellerBeanPayMapper INSTANCE = Mappers.getMapper(SellerBeanPayMapper.class);

	SellerBeanPayDto toDto(SellerBeanPay sellerBeanPay);
	SellerBeanPayResponse toResponse(SellerBeanPayDto sellerBeanPayDto);
}