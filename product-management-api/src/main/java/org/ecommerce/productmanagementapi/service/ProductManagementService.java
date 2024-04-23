package org.ecommerce.productmanagementapi.service;

import java.util.List;

import org.ecommerce.product.entity.Image;
import org.ecommerce.product.entity.Product;
import org.ecommerce.product.entity.SellerRep;
import org.ecommerce.productmanagementapi.repository.ImageRepository;
import org.ecommerce.productmanagementapi.repository.ProductRepository;
import org.ecommerce.productmanagementapi.dto.ProductManagementDto;
import org.ecommerce.productmanagementapi.dto.ProductManagementMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductManagementService {

	private final ProductRepository productRepository;

	private final ImageRepository imageRepository;
	private static final SellerRep test = new SellerRep(1,"TEST");

	/**
	 * 상품등록 로직
	 * <p>
	 * 상품을 등록하는 메서드 입니다
	 * <p>
	 * @author 홍종민
	 * @param product - 사용자가 등록을 요청한 데이터를 담은 Dto 입니다
	 * @return ProductManagementDto - 사용자에게 전달해주기 위한 Response Dto 입니다.
	 */
	public ProductManagementDto productRegister(ProductManagementDto.Request.Register product) {

		Product createProduct = Product.ofCreate(
			product.category(),
			product.price(),
			product.stock(),
			product.name(),
			product.bean(),
			product.acidity(),
			product.information(),
			product.isCrush(),
			product.isDecaf(),
			test
		);

		Product savedProduct = productRepository.save(createProduct);

		saveImages(product.images(),savedProduct);

		return ProductManagementMapper.INSTANCE.toDto(savedProduct);
	}

	private void saveImages(List<ProductManagementDto.Request.Register.ImageDto> imageDtos, Product savedProduct ) {
		List<Image> images = imageDtos.stream()
			.map(imageDto -> Image.ofCreate(imageDto.imageUrl(), imageDto.isThumbnail(), imageDto.sequenceNumber(),
				savedProduct))
			.toList();
		imageRepository.saveAll(images);
	}

}