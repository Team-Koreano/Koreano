package org.ecommerce.productapi.external.service;

import java.util.List;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.productapi.dto.ProductMapper;
import org.ecommerce.productapi.dto.ProductWithSellerRepAndImagesDto;
import org.ecommerce.productapi.dto.request.CreateProductRequest;
import org.ecommerce.productapi.dto.request.ModifyProductRequest;
import org.ecommerce.productapi.dto.request.ModifyProductsStatusRequest;
import org.ecommerce.productapi.dto.request.ModifyStockRequest;
import org.ecommerce.productapi.entity.Image;
import org.ecommerce.productapi.entity.Product;
import org.ecommerce.productapi.entity.SellerRep;
import org.ecommerce.productapi.entity.enumerated.ProductStatus;
import org.ecommerce.productapi.exception.ProductErrorCode;
import org.ecommerce.productapi.provider.S3Provider;
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

	/**
	 상품등록 로직
	 <p>
	 상품을 등록하는 메서드 입니다
	 <p>

	 @param product - 상품 등록 데이터
	 @return ProductManagementDto - 사용자에게 전달해주기 위한 Response Dto 입니다.
	 */
	public ProductWithSellerRepAndImagesDto productRegister(final CreateProductRequest product,
		MultipartFile thumbnailImage, final List<MultipartFile> images) {

		final Product createdProduct = Product.createProduct(
			product.category(),
			product.price(),
			product.stock(),
			product.name(),
			product.bean(),
			product.acidity(),
			product.information(),
			product.isCrush(),
			product.isDecaf(),
			product.size(),
			product.capacity(),
			product.deliveryFee(),
			seller
		);

		final Product savedProduct = productRepository.save(createdProduct);

		savedProduct.saveImages(s3Provider.uploadImageFiles(thumbnailImage, images).stream()
			.map(image -> Image.ofCreate(image.imageUrl(), image.isThumbnail(), image.sequenceNumber(), savedProduct))
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
	public ProductWithSellerRepAndImagesDto modifyToStatus(final Integer productId, final ProductStatus status) {
		Product product = productRepository.findProductById(productId);
		if (product == null) {
			throw new CustomException(ProductErrorCode.NOT_FOUND_PRODUCT);
		}

		product.toModifyStatus(status);

		return ProductMapper.INSTANCE.toDto(product);
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
	public ProductWithSellerRepAndImagesDto increaseToStock(ModifyStockRequest stock) {

		Product product = productRepository.findProductById(stock.productId());

		if (product == null) {
			throw new CustomException(ProductErrorCode.NOT_FOUND_PRODUCT);
		}
		product.increaseStock(stock.requestStock());

		return ProductMapper.INSTANCE.toDto(product);
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
	public ProductWithSellerRepAndImagesDto decreaseToStock(final ModifyStockRequest stock) {

		Product product = productRepository.findProductById(stock.productId());

		if (product == null)
			throw new CustomException(ProductErrorCode.NOT_FOUND_PRODUCT);
		if (!product.checkStock(stock.requestStock()))
			throw new CustomException(ProductErrorCode.CAN_NOT_BE_SET_TO_BELOW_ZERO);

		return ProductMapper.INSTANCE.toDto(product);
	}

	/**
	 상품 수정
	 <p>
	 상품을 수정하는 메서드입니다
	 <p>

	 @param modifyProduct - 상품을 수정 데이터
	 @return ProductManagementDto - 사용자에게 전달해주기 위한 Response Dto 입니다.
	 */
	public ProductWithSellerRepAndImagesDto modifyToProduct(
		final Integer productId,
		final ModifyProductRequest modifyProduct,
		MultipartFile thumbnailImage,
		List<MultipartFile> images
	) {

		Product product = productRepository.findProductById(productId);

		if (product == null)
			throw new CustomException(ProductErrorCode.NOT_FOUND_PRODUCT);

		if (thumbnailImage != null || images != null) {

			s3Provider.deleteFile(product.getImagesUrl());

			product.deleteImages();

			product.saveImages(s3Provider.uploadImageFiles(thumbnailImage, images).stream()
				.map(image -> Image.ofCreate(image.imageUrl(), image.isThumbnail(), image.sequenceNumber(),
					product))
				.toList());
		}

		product.toModify(
			modifyProduct.category(),
			modifyProduct.price(),
			modifyProduct.name(),
			modifyProduct.bean(),
			modifyProduct.acidity(),
			modifyProduct.information(),
			modifyProduct.isCrush(),
			modifyProduct.isDecaf(),
			modifyProduct.size(),
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
	public List<ProductWithSellerRepAndImagesDto> bulkModifyStatus(
		final ModifyProductsStatusRequest bulkStatus
	) {

		List<Product> products = productRepository.findProductsByIds(bulkStatus.productId());

		if (products.isEmpty()) {
			throw new CustomException(ProductErrorCode.NOT_FOUND_PRODUCT);
		}

		products.forEach(product -> product.toModifyStatus(bulkStatus.productStatus()));

		// return ProductMapper.INSTANCE.toDtos(products);
		return null;
	}

	/**
	 * @param productId 상품 ID
	 * @return ProductSearchDto
	 * @author ${no.oneho}
	 * product ID로 단일 레코드를 조회한다.
	 */
	@Transactional(readOnly = true)
	public ProductWithSellerRepAndImagesDto getProductById(final Integer productId) {

		final Product product = productRepository.findProductById(productId);

		if (product == null) {
			throw new CustomException(ProductErrorCode.NOT_FOUND_PRODUCT_ID);
		}

		return ProductMapper.INSTANCE.entityToDtoWithImageList(product);
	}
}
