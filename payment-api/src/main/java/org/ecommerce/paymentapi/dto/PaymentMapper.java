package org.ecommerce.paymentapi.dto;

import org.ecommerce.paymentapi.entity.Payment;
import org.ecommerce.paymentapi.entity.enumerate.ProcessStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PaymentMapper {

	PaymentMapper INSTANCE = Mappers.getMapper(PaymentMapper.class);

	@Mapping(source = "userBeanPay.userId", target = "userId")
	@Mapping(source = "status", target = "processStatus", qualifiedByName = "processEnum")
	PaymentDto toDto(Payment payment);

	@Named("processEnum")
	static ProcessStatus statusToEnum(String status) {
		return ProcessStatus.getProcessStatus(status);
	}
}