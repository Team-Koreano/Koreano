package org.ecommerce.productapi.external.controller;

import java.util.List;

import org.ecommerce.common.vo.Response;
import org.ecommerce.productapi.entity.enumerated.ProductStatus;
import org.ecommerce.productapi.external.service.ElasticSearchService;
import org.ecommerce.productapi.external.service.ProductService;
import org.springframework.http.HttpStatus;
import org.ecommerce.productapi.dto.response.*;
import org.ecommerce.productapi.dto.request.*;
import org.ecommerce.productapi.dto.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/external/product/v1")
@RequiredArgsConstructor
public class ProductController {

	private final ProductService productService;
	private final ElasticSearchService elasticSearchService;

	@PostMapping()
	public Response<ProductResponse> register(
		@Valid @RequestPart(value = "product") final CreateProductRequest product,
		@RequestPart(value = "thumbnailImage", required = false) final MultipartFile thumbnailImage,
		@RequestPart(value = "images", required = false) final List<MultipartFile> images) {
		ProductWithSellerRepAndImagesDto productManagementDto = productService.productRegister(product,
			thumbnailImage,
			images);
		return new Response<>(HttpStatus.OK.value(), ProductMapper.INSTANCE.toResponse(productManagementDto));
	}

	@PutMapping("/status/{productId}/{status}")
	public Response<ProductResponse> modifyToStatus(
		@PathVariable("productId") final Integer productId,
		@PathVariable("status") final ProductStatus status
	) {
		ProductWithSellerRepAndImagesDto productManagementDto = productService.modifyToStatus(productId,
			status);
		return new Response<>(HttpStatus.OK.value(), ProductMapper.INSTANCE.toResponse(productManagementDto));
	}

	@PutMapping("/status")
	public Response<List<ProductResponse>> bulkModifyStatus(
		@RequestBody final ModifyProductsStatusRequest bulkStatus
	) {
		final List<ProductWithSellerRepAndImagesDto> productManagementDto = productService.bulkModifyStatus(
			bulkStatus);
		return new Response<>(HttpStatus.OK.value(),
			ProductMapper.INSTANCE.toResponse(productManagementDto));
	}

	@PutMapping("/stock/increase")
	public Response<ProductResponse> increaseToStock(
		@Valid @RequestBody final ModifyStockRequest stock
	) {
		ProductWithSellerRepAndImagesDto productManagementDto = productService.increaseToStock(stock);
		return new Response<>(HttpStatus.OK.value(), ProductMapper.INSTANCE.toResponse(productManagementDto));
	}

	@PutMapping("/stock/decrease")
	public Response<ProductResponse> decreaseToStock(
		@Valid @RequestBody final ModifyStockRequest stock
	) {
		ProductWithSellerRepAndImagesDto productManagementDto = productService.decreaseToStock(stock);
		return new Response<>(HttpStatus.OK.value(), ProductMapper.INSTANCE.toResponse(productManagementDto));
	}

	@PutMapping("/{productId}")
	public Response<ProductResponse> modifyToProduct(
		@PathVariable("productId") final Integer productId,
		@Valid @RequestPart(value = "modifyProduct") final ModifyProductRequest modifyProduct,
		@RequestPart(value = "thumbnailImage", required = false) final MultipartFile thumbnailImage,
		@RequestPart(value = "images", required = false) final List<MultipartFile> images

	) {
		ProductWithSellerRepAndImagesDto productManagementDto = productService
			.modifyToProduct(productId, modifyProduct, thumbnailImage, images);

		return new Response<>(HttpStatus.OK.value(), ProductMapper.INSTANCE.toResponse(productManagementDto));
	}

	@GetMapping("/{productId}")
	public Response<DetailResponse> getProductById(
		@PathVariable("productId") final Integer productId) {

		return new Response<>(HttpStatus.OK.value(),
			DetailResponse.of(productService.getProductById(productId))
		);
	}

	@GetMapping("/suggest")
	public Response<List<SuggestedResponse>> suggestSearchKeyword(
		@RequestParam(value = "keyword") final String keyword) {

		final List<ProductDto> suggestedProducts = elasticSearchService.suggestSearchKeyword(keyword);

		return new Response<>(HttpStatus.OK.value(),
			suggestedProducts.stream()
				.map(SuggestedResponse::of)
				.toList());
	}

	@GetMapping("/search")
	public Response<SearchResponse> searchProducts(
		SearchRequest request,
		@RequestParam(required = false, defaultValue = "0", name = "pageNumber")
		Integer pageNumber,
		@RequestParam(required = false, defaultValue = "10", name = "pageSize")
		Integer pageSize
	) {

		final PagedSearchDto pagedSearchDto = elasticSearchService.searchProducts(request, pageNumber, pageSize);

		return new Response<>(HttpStatus.OK.value(),
			SearchResponse.of(pagedSearchDto));
	}
}
