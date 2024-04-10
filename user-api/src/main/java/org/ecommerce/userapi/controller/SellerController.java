package org.ecommerce.userapi.controller;

import org.ecommerce.common.vo.Response;
import org.ecommerce.userapi.dto.SellerDto;
import org.ecommerce.userapi.service.SellerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/seller")
public class SellerController {
	private final SellerService sellerService;

	@PostMapping("/register")
	public Response<SellerDto.Response.Register> register(@RequestBody SellerDto.Request.Register register) {
		SellerDto.Response.Register responseSeller = sellerService.registerSeller(register);
		return new Response<>(HttpStatus.OK.value(), responseSeller);
	}
}
