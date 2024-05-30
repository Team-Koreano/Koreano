package org.ecommerce.productmanagementapi.external;

import java.util.List;

import org.ecommerce.common.vo.Response;
import org.ecommerce.product.entity.enumerated.ProductStatus;
import org.ecommerce.productmanagementapi.dto.ProductManagementDtoWithImages;
import org.ecommerce.productmanagementapi.dto.ProductManagementMapper;
import org.ecommerce.productmanagementapi.dto.request.CreateProductRequest;
import org.ecommerce.productmanagementapi.dto.request.ModifyProductRequest;
import org.ecommerce.productmanagementapi.dto.request.ModifyProductsStatusRequest;
import org.ecommerce.productmanagementapi.dto.request.ModifyStockRequest;
import org.ecommerce.productmanagementapi.dto.response.ProductResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/external/product/v1")
@RequiredArgsConstructor
public class ProductManagementController {

	private final ProductManagementService productManagementService;

	@PostMapping()
	public Response<ProductResponse> register(
		@Valid @RequestPart(value = "product") final CreateProductRequest product,
		@RequestPart(value = "thumbnailImage", required = false) final MultipartFile thumbnailImage,
		@RequestPart(value = "images", required = false) final List<MultipartFile> images) {
		ProductManagementDtoWithImages productManagementDto = productManagementService.productRegister(product,
			thumbnailImage,
			images);
		return new Response<>(HttpStatus.OK.value(), ProductManagementMapper.INSTANCE.toResponse(productManagementDto));
	}

	@PutMapping("/status/{productId}/{status}")
	public Response<ProductResponse> modifyToStatus(
		@PathVariable("productId") final Integer productId,
		@PathVariable("status") final ProductStatus status
	) {
		ProductManagementDtoWithImages productManagementDto = productManagementService.modifyToStatus(productId,
			status);
		return new Response<>(HttpStatus.OK.value(), ProductManagementMapper.INSTANCE.toResponse(productManagementDto));
	}

	@PutMapping("/status")
	public Response<List<ProductResponse>> bulkModifyStatus(
		@RequestBody final ModifyProductsStatusRequest bulkStatus
	) {
		final List<ProductManagementDtoWithImages> productManagementDto = productManagementService.bulkModifyStatus(
			bulkStatus);
		return new Response<>(HttpStatus.OK.value(),
			ProductManagementMapper.INSTANCE.toResponse(productManagementDto));
	}

	@PutMapping("/stock/increase")
	public Response<ProductResponse> increaseToStock(
		@Valid @RequestBody final ModifyStockRequest stock
	) {
		ProductManagementDtoWithImages productManagementDto = productManagementService.increaseToStock(stock);
		return new Response<>(HttpStatus.OK.value(), ProductManagementMapper.INSTANCE.toResponse(productManagementDto));
	}

	@PutMapping("/stock/decrease")
	public Response<ProductResponse> decreaseToStock(
		@Valid @RequestBody final ModifyStockRequest stock
	) {
		ProductManagementDtoWithImages productManagementDto = productManagementService.decreaseToStock(stock);
		return new Response<>(HttpStatus.OK.value(), ProductManagementMapper.INSTANCE.toResponse(productManagementDto));
	}

	@PutMapping("/{productId}")
	public Response<ProductResponse> modifyToProduct(
		@PathVariable("productId") final Integer productId,
		@Valid @RequestPart(value = "modifyProduct") final ModifyProductRequest modifyProduct,
		@RequestPart(value = "thumbnailImage", required = false) final MultipartFile thumbnailImage,
		@RequestPart(value = "images", required = false) final List<MultipartFile> images

	) {
		ProductManagementDtoWithImages productManagementDto = productManagementService
			.modifyToProduct(productId, modifyProduct, thumbnailImage, images);

		return new Response<>(HttpStatus.OK.value(), ProductManagementMapper.INSTANCE.toResponse(productManagementDto));
	}
}
