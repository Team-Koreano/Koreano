package org.ecommerce.productapi.external.service;

import java.util.List;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.productapi.dto.ProductDetailDto;
import org.ecommerce.productapi.dto.ProductMapper;
import org.ecommerce.productapi.dto.ProductWithSellerRepAndImagesAndProductDetailsDto;
import org.ecommerce.productapi.dto.request.AddProductDetailRequest;
import org.ecommerce.productapi.dto.request.CreateProductRequest;
import org.ecommerce.productapi.dto.request.ModifyProductDetailRequest;
import org.ecommerce.productapi.dto.request.ModifyProductRequest;
import org.ecommerce.productapi.dto.request.ModifyProductsStatusRequest;
import org.ecommerce.productapi.dto.request.ModifyStockRequest;
import org.ecommerce.productapi.entity.Image;
import org.ecommerce.productapi.entity.Product;
import org.ecommerce.productapi.entity.ProductDetail;
import org.ecommerce.productapi.entity.SellerRep;
import org.ecommerce.productapi.entity.enumerated.ProductStatus;
import org.ecommerce.productapi.exception.ProductErrorCode;
import org.ecommerce.productapi.provider.S3Provider;
import org.ecommerce.productapi.repository.ProductDetailRepository;
import org.ecommerce.productapi.repository.ProductRepository;
import org.ecommerce.productapi.repository.SellerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {

	// TODO : 향후 Replication 후 삭제
	private static final SellerRep seller = new SellerRep(1, "TEST");
	private final ProductRepository productRepository;
	private final SellerRepository sellerRepository;
	private final S3Provider s3Provider;
	private final ProductDetailRepository productDetailRepository;

	/**
	 상품등록 로직
	 <p>
	 상품을 등록하는 메서드 입니다
	 <p>

	 @param createProductRequest - 상품 등록 데이터
	 @return ProductManagementDto - 사용자에게 전달해주기 위한 Response Dto 입니다.
	 */
	public ProductWithSellerRepAndImagesAndProductDetailsDto productRegister(
		final CreateProductRequest createProductRequest,
		final MultipartFile thumbnailImage,
		final List<MultipartFile> images
	) {

		if (createProductRequest.productDetails().isEmpty()) {
			throw new CustomException(ProductErrorCode.IS_NOT_ENOUGH_PRODUCT_DETAIL);
		}

		final Product createdProduct = Product.createProduct(
			createProductRequest.category(),
			createProductRequest.name(),
			createProductRequest.bean(),
			createProductRequest.acidity(),
			createProductRequest.information(),
			createProductRequest.isCrush(),
			createProductRequest.isDecaf(),
			createProductRequest.capacity(),
			createProductRequest.deliveryFee(),
			seller
		);

		final Product savedProduct = productRepository.save(createdProduct);

		savedProduct.saveProductDetails(
			createProductRequest.productDetails().stream().map(productDetailDto -> ProductDetail.ofCreate(
				savedProduct,
				productDetailDto.price(),
				productDetailDto.stock(),
				productDetailDto.size(),
				productDetailDto.isDefault(),
				productDetailDto.status())
			).toList()
		);

		savedProduct.saveImages(
			s3Provider.uploadImageFiles(thumbnailImage, images).stream()
				.map(image -> Image.ofCreate(image.imageUrl(), image.isThumbnail(), image.sequenceNumber(),
					savedProduct))
				.toList());

		return ProductMapper.INSTANCE.toDto(savedProduct);
	}

	/**
	 * 상품 상태 변경
	 * <p>
	 * 상품의 상태를 수정하는 메서드입니다
	 * <p>
	 * @author Hong
	 *
	 * @param productId - 상품의 식별값
	 * @param status - 상품의 변경할 상태값
	 * @return ProductManagementDto - 사용자에게 전달해주기 위한 Response Dto 입니다.
	 */
	public ProductWithSellerRepAndImagesAndProductDetailsDto modifyToStatus(final Integer productId,
		final ProductStatus status) {

		final Product product = productRepository.findProductWithProductDetailsById(productId);

		if (product == null) {
			throw new CustomException(ProductErrorCode.NOT_FOUND_PRODUCT);
		}

		product.toModifyStatus(status);

		return ProductMapper.INSTANCE.toDto(product);
	}

	/**
	 * ProductDetail 을 추가하는 메서드입니다
	 * @author Hong
	 *
	 * @param productId - Product 식별값입니다
	 * @param addProductDetailRequest - ProductDetail Request Dto 입니다
	 * @return - ProductDetailDto 입니다
	 */

	public ProductDetailDto addProductDetail(
		final Integer productId,
		final AddProductDetailRequest addProductDetailRequest
	) {
		final Product product = productRepository.findProductById(productId);

		if (product == null) {
			throw new CustomException(ProductErrorCode.NOT_FOUND_PRODUCT);
		}

		return ProductMapper.INSTANCE.toDto(
			product
				.addProductDetail(
					addProductDetailRequest.price(),
					addProductDetailRequest.stock(),
					addProductDetailRequest.size(),
					addProductDetailRequest.isDefault(),
					addProductDetailRequest.status()
				)
		);
	}

	/**
	 * ProductDetail 을 삭제하는 메서드입니다
	 * @author Hong
	 *
	 * @param productDetailId - ProductDetailId 의 식별값입니다
	 * @return String - 반환될 메세지입니다
	 */
	public String deleteProductDetail(final Integer productDetailId) {

		final ProductDetail productDetail = productDetailRepository.findByProductDetailId(productDetailId);

		if (productDetail == null) {
			throw new CustomException(ProductErrorCode.NOT_FOUND_PRODUCT_DETAIL);
		}

		final Product product = productRepository.findProductWithProductDetailsById(productDetail.getProduct().getId());

		if (product == null) {
			throw new CustomException(ProductErrorCode.NOT_FOUND_PRODUCT);
		}

		product.deleteProductDetail(productDetail);

		if (!product.checkHasEnoughDetails()) {
			throw new CustomException(ProductErrorCode.IS_NOT_ENOUGH_PRODUCT_DETAIL);
		}

		return "상품 디테일 삭제를 성공 하였습니다";
	}

	public ProductDetailDto modifyToProductDetailStatus(final Integer productDetailId,
		final ProductStatus status) {

		ProductDetail productDetail = productDetailRepository.findByProductDetailId(productDetailId);

		if (productDetail == null) {
			throw new CustomException(ProductErrorCode.NOT_FOUND_PRODUCT);
		}

		productDetail.toModifyStatus(status);

		return ProductMapper.INSTANCE.toDto(productDetail);
	}

	public ProductDetailDto modifyToProductDetail(final Integer productDetailId,
		final ModifyProductDetailRequest modifyProductDetailRequest) {

		final ProductDetail productDetail = productDetailRepository.findByProductDetailId(productDetailId);

		if (productDetail == null) {
			throw new CustomException(ProductErrorCode.NOT_FOUND_PRODUCT_DETAIL);
		}

		if (modifyProductDetailRequest.isDefault()) {
			productRepository.findProductWithProductDetailsById(
				productDetail.getProduct().getId()
			).changeDetailsIsDefault();
		}

		productDetail.toModifyProductDetail(
			modifyProductDetailRequest.price(),
			modifyProductDetailRequest.size(),
			modifyProductDetailRequest.isDefault()
		);

		return ProductMapper.INSTANCE.toDto(productDetail);
	}

	/**
	 * 상품 재고 변경
	 * <p>
	 * 상품의 재고를 증가시키는 메서드입니다
	 * <p>
	 * @author Hong
	 *
	 * @param stock - 상품의 재고값
	 * @return ProductManagementDto - 사용자에게 전달해주기 위한 Response Dto 입니다.
	 */
	public ProductDetailDto increaseToStock(ModifyStockRequest stock) {

		final ProductDetail productDetail = productDetailRepository.findByProductDetailId(stock.productDetailId());

		if (productDetail == null) {
			throw new CustomException(ProductErrorCode.NOT_FOUND_PRODUCT);
		}

		productDetail.increaseStock(stock.requestStock());

		return ProductMapper.INSTANCE.toDto(productDetail);
	}

	/**
	 * 상품 재고 변경
	 * <p>
	 * 상품의 재고를 감소시키는 메서드입니다
	 * <p>
	 * @author Hong
	 *
	 * @param stock - 상품의 재고값
	 * @return ProductManagementDto - 사용자에게 전달해주기 위한 Response Dto 입니다.
	 */
	public ProductDetailDto decreaseToStock(final ModifyStockRequest stock) {

		final ProductDetail productDetail = productDetailRepository.findByProductDetailId(stock.productDetailId());

		if (productDetail == null) {
			throw new CustomException(ProductErrorCode.NOT_FOUND_PRODUCT);
		}
		if (!productDetail.checkStock(stock.requestStock()))
			throw new CustomException(ProductErrorCode.CAN_NOT_BE_SET_TO_BELOW_ZERO);

		return ProductMapper.INSTANCE.toDto(productDetail);
	}

	/**
	 상품 수정
	 <p>
	 상품을 수정하는 메서드입니다
	 <p>

	 @param modifyProduct - 상품을 수정 데이터
	 @return ProductManagementDto - 사용자에게 전달해주기 위한 Response Dto 입니다.
	 */
	public ProductWithSellerRepAndImagesAndProductDetailsDto modifyToProduct(
		final Integer productId,
		final ModifyProductRequest modifyProduct,
		final MultipartFile thumbnailImage,
		final List<MultipartFile> images
	) {
		final Product product = productRepository.findProductWithProductDetailsById(productId);

		if (product == null) {
			throw new CustomException(ProductErrorCode.NOT_FOUND_PRODUCT);
		}

		if (thumbnailImage != null || !images.isEmpty()) {
			s3Provider.deleteFile(product.getImagesUrl());
			product.deleteImages();
			product.saveImages(s3Provider.uploadImageFiles(thumbnailImage, images).stream()
				.map(image -> Image.ofCreate(image.imageUrl(), image.isThumbnail(), image.sequenceNumber(), product))
				.toList());
		}

		product.toModify(
			modifyProduct.category(),
			modifyProduct.name(),
			modifyProduct.bean(),
			modifyProduct.acidity(),
			modifyProduct.information(),
			modifyProduct.isCrush(),
			modifyProduct.isDecaf(),
			modifyProduct.capacity(),
			modifyProduct.deliveryFee()
		);

		return ProductMapper.INSTANCE.toDto(product);
	}

	/**
	 * 여러 상품의 상태를 한번에 변경하는 메서드 입니다
	 * @author Hong
	 *
	 * @param bulkStatus - RequestDto 입니다.
	 * @return List<ProductManagementDto>
	 */
	public List<ProductWithSellerRepAndImagesAndProductDetailsDto> bulkModifyStatus(
		final ModifyProductsStatusRequest bulkStatus
	) {

		final List<Product> products = productRepository.findProductWithProductDetailsByIds(bulkStatus.productId());

		if (products.isEmpty()) {
			throw new CustomException(ProductErrorCode.NOT_FOUND_PRODUCT);
		}

		products.forEach(product -> product.toModifyStatus(bulkStatus.productStatus()));

		return ProductMapper.INSTANCE.toDtos(products);
	}

	/**
	 * @param productId 상품 ID
	 * @return ProductSearchDto
	 * @author ${no.oneho}
	 * product ID로 단일 레코드를 조회한다.
	 */
	@Transactional(readOnly = true)
	public ProductWithSellerRepAndImagesAndProductDetailsDto getProductById(final Integer productId) {

		final Product product = productRepository.findProductById(productId);

		if (product == null) {
			throw new CustomException(ProductErrorCode.NOT_FOUND_PRODUCT_ID);
		}

		return ProductMapper.INSTANCE.entityToDtoWithImageList(product);
	}
}
