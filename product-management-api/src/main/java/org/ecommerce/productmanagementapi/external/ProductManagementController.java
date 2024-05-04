package org.ecommerce.productmanagementapi.external;

import java.util.List;

import org.ecommerce.common.vo.Response;
import org.ecommerce.product.entity.enumerated.ProductStatus;
import org.ecommerce.productmanagementapi.dto.ProductManagementDto;
import org.ecommerce.productmanagementapi.dto.ProductManagementMapper;
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
	public Response<ProductManagementDto.Response> register(
		@Valid @RequestPart(value = "product") final ProductManagementDto.Request.Register product,
		@RequestPart(value = "thumbnailImage", required = false) final MultipartFile thumbnailImage,
		@RequestPart(value = "images", required = false) final List<MultipartFile> images) {
		ProductManagementDto productManagementDto = productManagementService.productRegister(product, thumbnailImage,
			images);
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

	@PutMapping("/stock/increase")
	public Response<ProductManagementDto.Response> increaseToStock(
		@Valid @RequestBody final ProductManagementDto.Request.Stock stock
	) {
		ProductManagementDto productManagementDto = productManagementService.increaseToStock(stock);
		return new Response<>(HttpStatus.OK.value(), ProductManagementMapper.INSTANCE.toResponse(productManagementDto));
	}

	@PutMapping("/stock/decrease")
	public Response<ProductManagementDto.Response> decreaseToStock(
		@Valid @RequestBody final ProductManagementDto.Request.Stock stock
	) {
		ProductManagementDto productManagementDto = productManagementService.decreaseToStock(stock);
		return new Response<>(HttpStatus.OK.value(), ProductManagementMapper.INSTANCE.toResponse(productManagementDto));
	}

	@PutMapping("/{productId}")
	public Response<ProductManagementDto.Response> modifyToProduct(
		@PathVariable("productId") final Integer productId,
		@Valid @RequestPart(value = "modifyProduct") final ProductManagementDto.Request.Modify modifyProduct,
		@RequestPart(value = "thumbnailImage", required = false) final MultipartFile thumbnailImage,
		@RequestPart(value = "images", required = false) final List<MultipartFile> images

	) {
		ProductManagementDto productManagementDto = productManagementService
			.modifyToProduct(productId, modifyProduct, thumbnailImage, images);

		return new Response<>(HttpStatus.OK.value(), ProductManagementMapper.INSTANCE.toResponse(productManagementDto));
	}
}
