package org.ecommerce.userapi.client;

import org.ecommerce.common.config.FeignConfig;
import org.ecommerce.userapi.dto.request.CreateBeanPayRequest;
import org.ecommerce.userapi.dto.request.DeleteBeanPayRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "seller-beanpay-service", url = "${payment-service.url}", configuration = FeignConfig.class)
public interface PaymentServiceClient {
	//beanPay 생성
	@PostMapping()
	void createSellerBeanPay(
		@RequestBody final CreateBeanPayRequest createBeanPayRequest
	);

	@DeleteMapping()
	void deleteSellerBeanPay(
		@RequestBody final DeleteBeanPayRequest deleteBeanPayRequest
	);

	@PostMapping()
	void createUserBeanPay(
		@RequestBody final CreateBeanPayRequest createBeanPayRequest
	);

	@DeleteMapping()
	void deleteUserBeanPay(
		@RequestBody final DeleteBeanPayRequest deleteBeanPayRequest
	);
}