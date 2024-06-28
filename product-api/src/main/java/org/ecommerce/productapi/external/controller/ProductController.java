package org.ecommerce.productapi.external.controller;

import java.util.List;

import org.ecommerce.common.security.AuthDetails;
import org.ecommerce.common.security.custom.CurrentUser;
import org.ecommerce.common.vo.Response;
import org.ecommerce.productapi.dto.PagedSearchDto;
import org.ecommerce.productapi.dto.ProductDto;
import org.ecommerce.productapi.dto.ProductMapper;
import org.ecommerce.productapi.dto.request.AddProductDetailRequest;
import org.ecommerce.productapi.dto.request.CreateProductRequest;
import org.ecommerce.productapi.dto.request.ModifyProductDetailRequest;
import org.ecommerce.productapi.dto.request.ModifyProductRequest;
import org.ecommerce.productapi.dto.request.ModifyProductsStatusRequest;
import org.ecommerce.productapi.dto.request.ModifyStockRequest;
import org.ecommerce.productapi.dto.request.SearchRequest;
import org.ecommerce.productapi.dto.response.DetailResponse;
import org.ecommerce.productapi.dto.response.ProductDetailResponse;
import org.ecommerce.productapi.dto.response.ProductResponse;
import org.ecommerce.productapi.dto.response.SearchResponse;
import org.ecommerce.productapi.dto.response.SuggestedResponse;
import org.ecommerce.productapi.entity.enumerated.ProductStatus;
import org.ecommerce.productapi.external.service.ElasticSearchService;
import org.ecommerce.productapi.external.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
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
		@Valid @RequestPart(value = "product") final CreateProductRequest createProductRequest,
		@RequestPart(value = "thumbnailImage", required = false) final MultipartFile thumbnailImage,
		@RequestPart(value = "images", required = false) final List<MultipartFile> images,
		@CurrentUser final AuthDetails authDetails
	) {
		return new Response<>(HttpStatus.OK.value(),
			ProductMapper.INSTANCE.toResponse(
				productService.productRegister(
					createProductRequest,
					thumbnailImage,
					authDetails.getId(),
					images
				)
			)
		);
	}

	@PostMapping("/detail/{productId}")
	public Response<ProductDetailResponse> addProductDetail(
		@Valid @RequestBody final AddProductDetailRequest addProductDetailRequest,
		@PathVariable(name = "productId") final Integer productId,
		@CurrentUser final AuthDetails authDetails
	) {
		return new Response<>(HttpStatus.OK.value(),
			ProductMapper.INSTANCE.toResponse(
				productService.addProductDetail(
					productId,
					addProductDetailRequest,
					authDetails.getId()
				)
			)
		);
	}

	@PutMapping("/detail/{productDetailId}")
	public Response<ProductDetailResponse> modifyProductDetail(
		@Valid @RequestBody final ModifyProductDetailRequest modifyProductDetailRequest,
		@PathVariable(name = "productDetailId") final Integer productDetailId,
		@CurrentUser final AuthDetails authDetails
	) {
		return new Response<>(HttpStatus.OK.value(),
			ProductMapper.INSTANCE.toResponse(
				productService.modifyToProductDetail(
					productDetailId,
					modifyProductDetailRequest,
					authDetails.getId()
				)
			)
		);
	}

	@PutMapping("/detail/{productDetailId}/{status}")
	public Response<ProductDetailResponse> modifyToDetailStatus(
		@PathVariable("productDetailId") final Integer productDetailId,
		@PathVariable("status") final ProductStatus status,
		@CurrentUser final AuthDetails authDetails
	) {
		return new Response<>(HttpStatus.OK.value(),
			ProductMapper.INSTANCE.toResponse(
				productService.modifyToProductDetailStatus(
					productDetailId,
					status,
					authDetails.getId()
				)
			)
		);
	}

	@DeleteMapping("detail/{productDetailId}")
	public Response<String> deleteProductDetail(
		@PathVariable(name = "productDetailId") final Integer productDetailId,
		@CurrentUser final AuthDetails authDetails
	) {
		return new Response<>(HttpStatus.OK.value(), (
			productService.deleteProductDetail(
				productDetailId,
				authDetails.getId())
		)
		);
	}

	@PutMapping("/{productId}/{status}")
	public Response<ProductResponse> modifyToStatus(
		@PathVariable("productId") final Integer productId,
		@PathVariable("status") final ProductStatus status,
		@CurrentUser final AuthDetails authDetails
	) {
		return new Response<>(HttpStatus.OK.value(),
			ProductMapper.INSTANCE.toResponse(
				productService.modifyToStatus(
					productId,
					status,
					authDetails.getId()
				)
			)
		);
	}

	@PutMapping("/status")
	public Response<List<ProductResponse>> bulkModifyStatus(
		@RequestBody final ModifyProductsStatusRequest bulkStatus,
		@CurrentUser final AuthDetails authDetails
	) {
		return new Response<>(HttpStatus.OK.value(),
			ProductMapper.INSTANCE.toResponse(
				productService.bulkModifyStatus(
					bulkStatus,
					authDetails.getId()
				)
			)
		);
	}

	@PutMapping("/detail/stock/increase")
	public Response<ProductDetailResponse> increaseToStock(
		@Valid @RequestBody final ModifyStockRequest stock,
		@CurrentUser final AuthDetails authDetails
	) {
		return new Response<>(HttpStatus.OK.value(),
			ProductMapper.INSTANCE.toResponse(
				productService.increaseToStock(
					stock,
					authDetails.getId()
				)
			)
		);
	}

	@PutMapping("/detail/stock/decrease")
	public Response<ProductDetailResponse> decreaseToStock(
		@Valid @RequestBody final ModifyStockRequest stock,
		@CurrentUser final AuthDetails authDetails
	) {
		return new Response<>(HttpStatus.OK.value(),
			ProductMapper.INSTANCE.toResponse(
				productService.decreaseToStock(
					stock,
					authDetails.getId()
				)
			)
		);
	}

	@PutMapping("/{productId}")
	public Response<ProductResponse> modifyToProduct(
		@PathVariable("productId") final Integer productId,
		@Valid @RequestPart(value = "modifyProduct") final ModifyProductRequest modifyProduct,
		@RequestPart(value = "thumbnailImage", required = false) final MultipartFile thumbnailImage,
		@RequestPart(value = "images", required = false) final List<MultipartFile> images,
		@CurrentUser final AuthDetails authDetails
	) {
		return new Response<>(HttpStatus.OK.value(),
			ProductMapper.INSTANCE.toResponse(
				productService.modifyToProduct(
					productId,
					modifyProduct,
					thumbnailImage,
					images,
					authDetails.getId()
				)
			)
		);
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
