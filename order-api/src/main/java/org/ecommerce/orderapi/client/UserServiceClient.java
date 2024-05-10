package org.ecommerce.orderapi.client;

import org.ecommerce.common.config.FeignConfig;
import org.ecommerce.orderapi.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "${user-service.url}", configuration = FeignConfig.class)
public interface UserServiceClient {

	@GetMapping("/{userId}")
	UserDto.Response getUser(
			@PathVariable("userId") final Integer userId);
}
