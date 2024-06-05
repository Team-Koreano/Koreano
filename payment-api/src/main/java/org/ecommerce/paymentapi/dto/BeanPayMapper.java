package org.ecommerce.paymentapi.dto;

import org.ecommerce.paymentapi.dto.response.BeanPayResponse;
import org.ecommerce.paymentapi.entity.BeanPay;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BeanPayMapper {

	BeanPayMapper INSTANCE = Mappers.getMapper(BeanPayMapper.class);

	BeanPayDto toDto(BeanPay beanPay);
	BeanPayResponse toResponse(BeanPayDto beanPayDto);


}