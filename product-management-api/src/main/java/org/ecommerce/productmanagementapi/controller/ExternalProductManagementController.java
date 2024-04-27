package org.ecommerce.productmanagementapi.controller;

import org.ecommerce.common.vo.Response;
import org.ecommerce.product.entity.type.ProductStatus;
import org.ecommerce.productmanagementapi.dto.ProductManagementDto;
import org.ecommerce.productmanagementapi.dto.ProductManagementMapper;
import org.ecommerce.productmanagementapi.service.ProductManagementService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/external/product-management/v1")
@RequiredArgsConstructor
public class ExternalProductManagementController {

	private final ProductManagementService productManagementService;
	// TODO : S3를 통한 이미지 저장 로직 추가
	// TODO : 상품 UD, 예외 사항 추가,

	@PostMapping()
	public Response<ProductManagementDto.Response> register(
		@Valid @RequestBody ProductManagementDto.Request.Register product) {
		ProductManagementDto productManagementDto = productManagementService.productRegister(product);
		return new Response<>(HttpStatus.OK.value(), ProductManagementMapper.INSTANCE.toResponse(productManagementDto));
	}

	@PutMapping("/status/{productId}/{status}")
	public Response<ProductManagementDto.Response> modifyToStatus(
		@PathVariable("productId") final Integer productId,
		@PathVariable("status") final ProductStatus status
	) {
		ProductManagementDto productManagementDto = productManagementService.modifyToStatus(productId, status);
		return new Response<>(HttpStatus.OK.value(), ProductManagementMapper.INSTANCE.toResponse(productManagementDto));
	}

	@PutMapping("/stock")
	public Response<ProductManagementDto.Response> modifyToStock(
		@RequestBody final ProductManagementDto.Request.Stock stock
	) {
		ProductManagementDto productManagementDto = productManagementService.modifyToStock(stock);
		return new Response<>(HttpStatus.OK.value(), ProductManagementMapper.INSTANCE.toResponse(productManagementDto));
	}

	@PutMapping("/{productId}")
	public Response<ProductManagementDto.Response> modifyToProduct(
		@PathVariable("productId") final Integer productId,
		@RequestBody final ProductManagementDto.Request.Modify modifyProduct
	) {
		ProductManagementDto productManagementDto = productManagementService.modifyToProduct(productId, modifyProduct);
		return new Response<>(HttpStatus.OK.value(), ProductManagementMapper.INSTANCE.toResponse(productManagementDto));
	}
}
