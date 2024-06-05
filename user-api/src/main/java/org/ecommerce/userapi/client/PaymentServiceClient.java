package org.ecommerce.userapi.client;

import org.ecommerce.common.config.FeignConfig;
import org.ecommerce.userapi.dto.request.CreateSellerBeanPayRequest;
import org.ecommerce.userapi.dto.request.CreateUserBeanPayRequest;
import org.ecommerce.userapi.dto.request.DeleteSellerBeanPayRequest;
import org.ecommerce.userapi.dto.request.DeleteUserBeanPayRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "beanpay-service", url = "${payment-service.url}", configuration = FeignConfig.class)
public interface PaymentServiceClient {
	//beanPay 생성
	@PostMapping("/seller")
	void createSellerBeanPay(
		@RequestBody final CreateSellerBeanPayRequest createSellerBeanPayRequest
	);

	@DeleteMapping("/seller")
	void deleteSellerBeanPay(
		@RequestBody final DeleteSellerBeanPayRequest deleteSellerBeanPayRequest
	);

	@PostMapping("/user")
	void createUserBeanPay(
		@RequestBody final CreateUserBeanPayRequest createUserBeanPayRequest
	);

	@DeleteMapping("/user")
	void deleteUserBeanPay(
		@RequestBody final DeleteUserBeanPayRequest deleteSellerBeanPayRequest
	);
}
