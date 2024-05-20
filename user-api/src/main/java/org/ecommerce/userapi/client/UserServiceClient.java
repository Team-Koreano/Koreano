package org.ecommerce.userapi.client;

import org.ecommerce.common.config.FeignConfig;
import org.ecommerce.userapi.dto.BeanPayDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-beanpay-service", url = "${payment-service.url}", configuration = FeignConfig.class)
public interface UserServiceClient {
	//beanPay 생성
	@PostMapping()
	BeanPayDto createBeanPay(
		@RequestBody final BeanPayDto.Request.CreateBeanPay createBeanPay
	);
}
