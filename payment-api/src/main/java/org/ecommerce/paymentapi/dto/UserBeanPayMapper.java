package org.ecommerce.paymentapi.dto;

import org.ecommerce.paymentapi.dto.response.UserBeanPayResponse;
import org.ecommerce.paymentapi.entity.UserBeanPay;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserBeanPayMapper {

	UserBeanPayMapper INSTANCE = Mappers.getMapper(UserBeanPayMapper.class);

	UserBeanPayDto toDto(UserBeanPay userBeanPay);
	UserBeanPayResponse toResponse(UserBeanPayDto userBeanPayDto);


}