package org.ecommerce.productmanagementapi.controller;

import org.ecommerce.common.vo.Response;
import org.ecommerce.productmanagementapi.dto.ProductManagementDto;
import org.ecommerce.productmanagementapi.service.ProductManagementService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/productmanagement/v1")
@RequiredArgsConstructor
public class ProductManagementController {

	private final ProductManagementService productManagementService;
	// TODO : S3를 통한 이미지 저장 로직 추가
	// TODO : 상품 UD, 예외 사항 추가,


	@PostMapping()
	public Response<ProductManagementDto.Response> register(
		@Valid @RequestBody ProductManagementDto.Request.Register product) {
		ProductManagementDto productManagementDto = productManagementService.productRegister(product);
		return new Response<>(HttpStatus.OK.value(), ProductManagementDto.Response.of(productManagementDto));
	}
}
