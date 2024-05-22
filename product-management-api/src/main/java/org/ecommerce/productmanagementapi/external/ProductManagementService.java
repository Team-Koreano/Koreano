package org.ecommerce.productmanagementapi.external;

import java.util.List;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.product.entity.Image;
import org.ecommerce.product.entity.Product;
import org.ecommerce.product.entity.SellerRep;
import org.ecommerce.product.entity.enumerated.ProductStatus;
import org.ecommerce.productmanagementapi.dto.ProductManagementDto;
import org.ecommerce.productmanagementapi.dto.ProductManagementMapper;
import org.ecommerce.productmanagementapi.exception.ProductManagementErrorCode;
import org.ecommerce.productmanagementapi.provider.S3Provider;
import org.ecommerce.productmanagementapi.repository.ProductRepository;
import org.ecommerce.productmanagementapi.repository.SellerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductManagementService {

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
	public ProductManagementDto productRegister(final ProductManagementDto.Request.Register product,
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
			seller
		);

		final Product savedProduct = productRepository.save(createdProduct);

		savedProduct.saveImages(s3Provider.uploadImageFiles(thumbnailImage, images).stream()
			.map(image -> Image.ofCreate(image.imageUrl(), image.isThumbnail(), image.sequenceNumber(), savedProduct))
			.toList());

		return ProductManagementMapper.INSTANCE.toDto(savedProduct);
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
	public ProductManagementDto modifyToStatus(final Integer productId, final ProductStatus status) {
		Product product = productRepository.findProductById(productId)
			.orElseThrow(() -> new CustomException(ProductManagementErrorCode.NOT_FOUND_PRODUCT));

		product.toModifyStatus(status);

		return ProductManagementMapper.INSTANCE.toDto(product);
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
	public ProductManagementDto increaseToStock(ProductManagementDto.Request.Stock stock) {
		Product product = productRepository.findProductById(stock.productId())
			.orElseThrow(() -> new CustomException(ProductManagementErrorCode.NOT_FOUND_PRODUCT));

		product.increaseStock(stock.requestStock());

		return ProductManagementMapper.INSTANCE.toDto(product);
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
	public ProductManagementDto decreaseToStock(final ProductManagementDto.Request.Stock stock) {
		Product product = productRepository.findProductById(stock.productId())
			.orElseThrow(() -> new CustomException(ProductManagementErrorCode.NOT_FOUND_PRODUCT));

		if (!product.checkStock(stock.requestStock())) {
			throw new CustomException(ProductManagementErrorCode.CAN_NOT_BE_SET_TO_BELOW_ZERO);
		}

		return ProductManagementMapper.INSTANCE.toDto(product);
	}

	/**
	 상품 수정
	 <p>
	 상품을 수정하는 메서드입니다
	 <p>

	 @param modifyProduct - 상품을 수정 데이터
	 @return ProductManagementDto - 사용자에게 전달해주기 위한 Response Dto 입니다.
	 */
	public ProductManagementDto modifyToProduct(
		final Integer productId,
		final ProductManagementDto.Request.Modify modifyProduct,
		MultipartFile thumbnailImage,
		List<MultipartFile> images
	) {

		Product product = productRepository.findProductById(productId)
			.orElseThrow(() -> new CustomException(ProductManagementErrorCode.NOT_FOUND_PRODUCT));

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
			modifyProduct.capacity()
		);

		if (thumbnailImage != null || images != null) {
			s3Provider.deleteFile(product.getImagesUrl());

			product.deleteImages();

			product.saveImages(s3Provider.uploadImageFiles(thumbnailImage, images).stream()
				.map(image -> Image.ofCreate(image.imageUrl(), image.isThumbnail(), image.sequenceNumber(),
					product))
				.toList());
		}
		return ProductManagementMapper.INSTANCE.toDto(product);
	}

	/**
	 * 여러 상품의 상태를 한번에 변경하는 메서드 입니다
	 * @author Hong
	 *
	 * @param bulkStatus - RequestDto 입니다.
	 * @return List<ProductManagementDto>
	 */
	public List<ProductManagementDto> bulkModifyStatus(
		final ProductManagementDto.Request.BulkStatus bulkStatus
	) {

		List<Product> products = productRepository.findProductsById(bulkStatus.productId());

		if (products.isEmpty()) {
			throw new CustomException(ProductManagementErrorCode.NOT_FOUND_PRODUCT);
		}

		products.forEach(product -> product.toModifyStatus(bulkStatus.productStatus()));

		return ProductManagementMapper.INSTANCE.productsToDtos(products);
	}
}
