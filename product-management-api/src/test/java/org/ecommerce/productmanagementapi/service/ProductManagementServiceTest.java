package org.ecommerce.productmanagementapi.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.product.entity.Image;
import org.ecommerce.product.entity.Product;
import org.ecommerce.product.entity.SellerRep;
import org.ecommerce.product.entity.enumerated.Acidity;
import org.ecommerce.product.entity.enumerated.Bean;
import org.ecommerce.product.entity.enumerated.ProductCategory;
import org.ecommerce.product.entity.enumerated.ProductStatus;
import org.ecommerce.productmanagementapi.dto.ImageDto;
import org.ecommerce.productmanagementapi.dto.ProductWithSellerRepAndImagesDto;
import org.ecommerce.productmanagementapi.dto.request.CreateProductRequest;
import org.ecommerce.productmanagementapi.dto.request.ModifyProductRequest;
import org.ecommerce.productmanagementapi.dto.request.ModifyProductsStatusRequest;
import org.ecommerce.productmanagementapi.dto.request.ModifyStockRequest;
import org.ecommerce.productmanagementapi.exception.ProductManagementErrorCode;
import org.ecommerce.productmanagementapi.external.ProductManagementService;
import org.ecommerce.productmanagementapi.provider.S3Provider;
import org.ecommerce.productmanagementapi.repository.ImageRepository;
import org.ecommerce.productmanagementapi.repository.ProductRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class ProductManagementServiceTest {

	private static final LocalDateTime testTime = LocalDateTime.
		parse("2024-04-14T17:41:52+09:00",
			DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"));
	private static final SellerRep seller = new SellerRep(1, "TEST");
	@InjectMocks
	private ProductManagementService productManagementService;
	@Mock
	private ProductRepository productRepository;
	@Mock
	private ImageRepository imageRepository;

	@Mock
	private S3Provider s3Provider;

	@Nested
	class 상품_등록_API {
		@Test
		void 원두_상품_등록() {
			final List<ImageDto> imageDtos = List.of(
				new ImageDto("image1.jpg", (short)1, true),
				new ImageDto("image2.jpg", (short)2, false),
				new ImageDto("image3.jpg", (short)3, false)
			);

			final CreateProductRequest productDtos =
				new CreateProductRequest(
					true,
					1000,
					50,
					Acidity.CINNAMON,
					Bean.ARABICA,
					ProductCategory.BEAN,
					"정말 맛있는 원두 단돈 천원",
					"부산 진구 유명가수가 좋아하는 원두",
					false,
					null,
					null,
					(short)3000
				);

			final Product product = Product.createProduct(
				productDtos.category(),
				productDtos.price(),
				productDtos.stock(),
				productDtos.name(),
				productDtos.bean(),
				productDtos.acidity(),
				productDtos.information(),
				productDtos.isCrush(),
				productDtos.isDecaf(),
				productDtos.size(),
				productDtos.capacity(),
				productDtos.deliveryFee(),
				seller
			);
			given(productRepository.save(any(Product.class))).willReturn(
				product
			);
			final ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);

			final MockMultipartFile mockThumbnailImage = new MockMultipartFile("thumbnailImage", "test.txt",
				"multipart/form-data",
				"test file".getBytes(StandardCharsets.UTF_8));

			List<MultipartFile> mockMultipartFiles = new ArrayList<>();

			final MockMultipartFile mockMultipartFile = new MockMultipartFile("images", "test2.txt",
				"multipart/form-data",
				"test file2".getBytes(StandardCharsets.UTF_8));
			mockMultipartFiles.add(mockMultipartFile);

			when(s3Provider.uploadImageFiles(mockThumbnailImage, mockMultipartFiles)).thenReturn(imageDtos);

			final ProductWithSellerRepAndImagesDto productWithSellerRepAndImagesDto = productManagementService.productRegister(
				productDtos,
				mockThumbnailImage,
				mockMultipartFiles);

			verify(productRepository, times(1)).save(captor.capture());

			Product productValue = captor.getValue();
			assertThat(productWithSellerRepAndImagesDto.acidity()).isEqualTo(productValue.getAcidity());
			assertThat(productWithSellerRepAndImagesDto.bean()).isEqualTo(productValue.getBean());
			assertThat(productWithSellerRepAndImagesDto.category()).isEqualTo(productValue.getCategory());
			assertThat(productWithSellerRepAndImagesDto.information()).isEqualTo(productValue.getInformation());
			assertThat(productWithSellerRepAndImagesDto.name()).isEqualTo(productValue.getName());
			assertThat(productWithSellerRepAndImagesDto.price()).isEqualTo(productValue.getPrice());
			assertThat(productWithSellerRepAndImagesDto.stock()).isEqualTo(productValue.getStock());
			assertThat(productWithSellerRepAndImagesDto.sellerRep()).usingRecursiveComparison()
				.isEqualTo(productValue.getSellerRep());
			assertThat(productWithSellerRepAndImagesDto.isCrush()).isEqualTo(productValue.getIsCrush());
			assertThat(productWithSellerRepAndImagesDto.isDecaf()).isEqualTo(productValue.getIsDecaf());
		}

		@Test
		void 디폴트_상품_등록() {
			final List<ImageDto> imageDtos = List.of(
				new ImageDto("image1.jpg", (short)1, true),
				new ImageDto("image2.jpg", (short)2, false),
				new ImageDto("image3.jpg", (short)3, false)
			);

			final CreateProductRequest productDtos =
				new CreateProductRequest(
					true,
					1000,
					50,
					Acidity.CINNAMON,
					Bean.ARABICA,
					ProductCategory.BEAN,
					"정말 맛있는 원두 단돈 천원",
					"부산 진구 유명가수가 좋아하는 원두",
					false,
					null,
					null,
					(short)3000);

			final Product product = Product.createProduct(
				productDtos.category(),
				productDtos.price(),
				productDtos.stock(),
				productDtos.name(),
				productDtos.bean(),
				productDtos.acidity(),
				productDtos.information(),
				productDtos.isCrush(),
				productDtos.isDecaf(),
				productDtos.size(),
				productDtos.capacity(),
				productDtos.deliveryFee(),
				seller
			);
			given(productRepository.save(any(Product.class))).willReturn(
				product
			);

			final ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);

			final MockMultipartFile mockThumbnailImage = new MockMultipartFile("thumbnailImage", "test.txt",
				"multipart/form-data",
				"test file".getBytes(StandardCharsets.UTF_8));

			List<MultipartFile> mockMultipartFiles = new ArrayList<>();

			final MockMultipartFile mockMultipartFile = new MockMultipartFile("images", "test2.txt",
				"multipart/form-data",
				"test file2".getBytes(StandardCharsets.UTF_8));
			mockMultipartFiles.add(mockMultipartFile);

			when(s3Provider.uploadImageFiles(mockThumbnailImage, mockMultipartFiles)).thenReturn(imageDtos);

			ProductWithSellerRepAndImagesDto productWithSellerRepAndImagesDto = productManagementService.productRegister(
				productDtos,
				mockThumbnailImage,
				mockMultipartFiles);

			verify(productRepository, times(1)).save(captor.capture());

			Product productValue = captor.getValue();
			assertThat(productWithSellerRepAndImagesDto.category()).isEqualTo(productValue.getCategory());
			assertThat(productWithSellerRepAndImagesDto.information()).isEqualTo(productValue.getInformation());
			assertThat(productWithSellerRepAndImagesDto.name()).isEqualTo(productValue.getName());
			assertThat(productWithSellerRepAndImagesDto.price()).isEqualTo(productValue.getPrice());
			assertThat(productWithSellerRepAndImagesDto.stock()).isEqualTo(productValue.getStock());
			assertThat(productWithSellerRepAndImagesDto.sellerRep()).usingRecursiveComparison()
				.isEqualTo(productValue.getSellerRep());
			assertThat(productWithSellerRepAndImagesDto.size()).isEqualTo(productValue.getSize());
		}
	}

	@Nested
	class 상품_상태_변경 {
		@Test
		void 상품_상태_변경_성공() {

			final Integer productId = 1;

			final ProductStatus newStatus = ProductStatus.DISCONTINUED;

			final Product entity = new Product(
				productId, ProductCategory.BEAN, 1000, 50, seller, 0, false,
				"정말 맛있는 원두 단돈 천원", Bean.ARABICA, Acidity.CINNAMON, "부산 진구 유명가수가 좋아하는 원두",
				true, null, null, ProductStatus.AVAILABLE, testTime, testTime, (short)3000, null
			);
			given(productRepository.save(any(Product.class))).willReturn(entity);

			when(productRepository.findProductById((productId))).thenReturn(entity);

			entity.toModifyStatus(newStatus);

			ProductWithSellerRepAndImagesDto result = productManagementService.modifyToStatus(
				productId, newStatus);

			assertThat(result.status()).isEqualTo(newStatus);
		}

		@Test
		void 상품_여러개_상태_변경_성공() {

			final List<Integer> productIds = List.of(1, 2);

			final ProductStatus newStatus = ProductStatus.DISCONTINUED;

			final Product entity1 = new Product(
				1, ProductCategory.BEAN, 1000, 50, seller, 0, false,
				"정말 맛있는 원두 단돈 천원", Bean.ARABICA, Acidity.CINNAMON, "부산 진구 유명가수가 좋아하는 원두",
				true, null, null, ProductStatus.AVAILABLE, testTime, testTime, (short)3000, null
			);

			final Product entity2 = new Product(
				2, ProductCategory.BEAN, 1000, 50, seller, 0, false,
				"정말 맛있는 원두 단돈 천원", Bean.ARABICA, Acidity.CINNAMON, "부산 진구 유명가수가 좋아하는 원두",
				true, null, null, ProductStatus.AVAILABLE, testTime, testTime, (short)3000, null
			);

			List<Product> products = List.of(entity1, entity2);

			given(productRepository.findProductsByIds(productIds)).willReturn(products);
			given(productRepository.saveAll(products)).willReturn(products);

			ModifyProductsStatusRequest request = new ModifyProductsStatusRequest(productIds,
				newStatus);

			List<ProductWithSellerRepAndImagesDto> result = productManagementService.bulkModifyStatus(request);

			assertThat(result).hasSize(2);
			assertThat(result.get(0).status()).isEqualTo(newStatus);
			assertThat(result.get(1).status()).isEqualTo(newStatus);
		}
	}

	@Nested
	class 상품_재고_변경 {
		@Test
		void 상품_재고_변경_성공() {

			final Integer productId = 1;

			final Integer stock = 20;

			final Integer existStock = 50;

			ModifyStockRequest request = new ModifyStockRequest(productId, stock);

			final Product entity = new Product(
				productId, ProductCategory.BEAN, 1000, existStock, seller, 0, false,
				"정말 맛있는 원두 단돈 천원", Bean.ARABICA, Acidity.CINNAMON, "부산 진구 유명가수가 좋아하는 원두",
				true, null, null, ProductStatus.AVAILABLE, testTime, testTime, (short)3000, null
			);

			given(productRepository.save(any(Product.class))).willReturn(entity);

			when(productRepository.findProductById((productId))).thenReturn(entity);

			ProductWithSellerRepAndImagesDto result = productManagementService.increaseToStock(
				request);

			assertThat(result.stock()).isEqualTo(existStock + stock);
		}

		@Test
		void 상품_재고_변경_실패() {

			final Integer productId = 1;

			final Integer stock = 40;

			final Integer existStock = 30;

			ModifyStockRequest request = new ModifyStockRequest(productId, stock);

			final Product entity = new Product(
				productId, ProductCategory.BEAN, 1000, existStock, seller, 0, false,
				"정말 맛있는 원두 단돈 천원", Bean.ARABICA, Acidity.CINNAMON, "부산 진구 유명가수가 좋아하는 원두",
				true, null, null, ProductStatus.AVAILABLE, testTime, testTime, (short)3000, null
			);

			given(productRepository.save(any(Product.class))).willReturn(entity);

			when(productRepository.findProductById((productId))).thenReturn(entity);

			assertThatThrownBy(() -> productManagementService.decreaseToStock(request))
				.isInstanceOf(CustomException.class)
				.hasMessage(ProductManagementErrorCode.CAN_NOT_BE_SET_TO_BELOW_ZERO.getMessage());
		}
	}

	@Test
	void 상품_수정() {

		final List<ImageDto> imageDtos = List.of(
			new ImageDto("image1.jpg", (short)1, true),
			new ImageDto("image2.jpg", (short)2, false),
			new ImageDto("image3.jpg", (short)3, false)
		);

		final Integer productId = 1;

		final ModifyProductRequest dto = new ModifyProductRequest(
			true, 10000, Acidity.CINNAMON, Bean.ARABICA, ProductCategory.BEAN,
			"수정된", "커피", null, null, true, (short)3000);

		final Image image = Image.ofCreate(
			"test",
			true,
			(short)1,
			null
		);
		List<Image> mockImages = new ArrayList<>();

		mockImages.add(image);

		Product entity = new Product(
			productId, ProductCategory.BEAN, 1000, 30, seller, 0, false,
			"정말 맛있는 원두 단돈 천원", Bean.ARABICA, Acidity.CINNAMON, "부산 진구 유명가수가 좋아하는 원두",
			true, null, null, ProductStatus.AVAILABLE, testTime, testTime, (short)3000, mockImages
		);

		entity.getImages().add(image);

		when(productRepository.findProductById((productId))).thenReturn(entity);

		final MockMultipartFile mockThumbnailImage = new MockMultipartFile("thumbnailImage", "test.txt",
			"multipart/form-data",
			"test file".getBytes(StandardCharsets.UTF_8));

		List<MultipartFile> mockMultipartFiles = new ArrayList<>();

		final MockMultipartFile mockMultipartFile = new MockMultipartFile("images", "test2.txt", "multipart/form-data",
			"test file2".getBytes(StandardCharsets.UTF_8));

		mockMultipartFiles.add(mockMultipartFile);

		when(s3Provider.uploadImageFiles(mockThumbnailImage, mockMultipartFiles)).thenReturn(imageDtos);

		ProductWithSellerRepAndImagesDto resultDto = productManagementService.modifyToProduct(
			productId, dto, mockThumbnailImage,
			mockMultipartFiles);

		verify(productRepository).findProductById((productId));

		assertThat(resultDto.acidity()).isEqualTo(dto.acidity());
		assertThat(resultDto.bean()).isEqualTo(dto.bean());
		assertThat(resultDto.category()).isEqualTo(dto.category());
		assertThat(resultDto.information()).isEqualTo(dto.information());
		assertThat(resultDto.name()).isEqualTo(dto.name());
		assertThat(resultDto.price()).isEqualTo(dto.price());
		assertThat(resultDto.isCrush()).isEqualTo(dto.isCrush());
		assertThat(resultDto.isDecaf()).isEqualTo(dto.isDecaf());
	}
}
