package org.ecommerce.productmanagementapi.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.product.entity.Image;
import org.ecommerce.product.entity.Product;
import org.ecommerce.product.entity.SellerRep;
import org.ecommerce.product.entity.enumerated.Acidity;
import org.ecommerce.product.entity.enumerated.Bean;
import org.ecommerce.product.entity.enumerated.ProductCategory;
import org.ecommerce.product.entity.enumerated.ProductStatus;
import org.ecommerce.productmanagementapi.dto.ProductManagementDto;
import org.ecommerce.productmanagementapi.exception.ProductManagementErrorCode;
import org.ecommerce.productmanagementapi.external.ProductManagementService;
import org.ecommerce.productmanagementapi.provider.S3Provider;
import org.ecommerce.productmanagementapi.repository.ImageRepository;
import org.ecommerce.productmanagementapi.repository.ProductRepository;
import org.ecommerce.productmanagementapi.util.ProductFactory;
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
			final List<ProductManagementDto.Request.Image> imageDtos = List.of(
				new ProductManagementDto.Request.Image("image1.jpg", (short)1, true),
				new ProductManagementDto.Request.Image("image2.jpg", (short)2, false),
				new ProductManagementDto.Request.Image("image3.jpg", (short)3, false)
			);

			final ProductManagementDto.Request.Register productDtos =
				new ProductManagementDto.Request.Register(
					true,
					1000,
					50,
					Acidity.CINNAMON,
					Bean.ARABICA,
					ProductCategory.BEAN,
					"정말 맛있는 원두 단돈 천원",
					"부산 진구 유명가수가 좋아하는 원두",
					false,
					null
				);

			final Product product = ProductFactory
				.getFactory(productDtos.category())
				.createProduct(
					productDtos, seller
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

			final ProductManagementDto productManagementDto = productManagementService.productRegister(productDtos,
				mockThumbnailImage,
				mockMultipartFiles);

			verify(productRepository, times(1)).save(captor.capture());

			Product productValue = captor.getValue();
			assertThat(productManagementDto.getAcidity()).isEqualTo(productValue.getAcidity());
			assertThat(productManagementDto.getBean()).isEqualTo(productValue.getBean());
			assertThat(productManagementDto.getCategory()).isEqualTo(productValue.getCategory());
			assertThat(productManagementDto.getInformation()).isEqualTo(productValue.getInformation());
			assertThat(productManagementDto.getName()).isEqualTo(productValue.getName());
			assertThat(productManagementDto.getPrice()).isEqualTo(productValue.getPrice());
			assertThat(productManagementDto.getStock()).isEqualTo(productValue.getStock());
			assertThat(productManagementDto.getSellerRep()).usingRecursiveComparison()
				.isEqualTo(productValue.getSellerRep());
			assertThat(productManagementDto.getIsCrush()).isEqualTo(productValue.getIsCrush());
			assertThat(productManagementDto.getIsDecaf()).isEqualTo(productValue.getIsDecaf());
		}

		@Test
		void 디폴트_상품_등록() {
			final List<ProductManagementDto.Request.Image> imageDtos = List.of(
				new ProductManagementDto.Request.Image("image1.jpg", (short)1, true),
				new ProductManagementDto.Request.Image("image2.jpg", (short)2, false),
				new ProductManagementDto.Request.Image("image3.jpg", (short)3, false)
			);

			final ProductManagementDto.Request.Register productDtos =
				new ProductManagementDto.Request.Register(
					null,
					1000,
					50,
					null,
					null,
					ProductCategory.BLENDER,
					"잘 갈리는 블렌더",
					"잘 블",
					null,
					"20 * 60 500ml"
				);

			ProductFactory factory = ProductFactory
				.getFactory(productDtos.category());
			final Product product = factory
				.createProduct(
					productDtos, seller
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

			final ProductManagementDto productManagementDto = productManagementService.productRegister(productDtos,
				mockThumbnailImage,
				mockMultipartFiles);

			verify(productRepository, times(1)).save(captor.capture());

			Product productValue = captor.getValue();
			assertThat(productManagementDto.getCategory()).isEqualTo(productValue.getCategory());
			assertThat(productManagementDto.getInformation()).isEqualTo(productValue.getInformation());
			assertThat(productManagementDto.getName()).isEqualTo(productValue.getName());
			assertThat(productManagementDto.getPrice()).isEqualTo(productValue.getPrice());
			assertThat(productManagementDto.getStock()).isEqualTo(productValue.getStock());
			assertThat(productManagementDto.getSellerRep()).usingRecursiveComparison()
				.isEqualTo(productValue.getSellerRep());
			assertThat(productManagementDto.getSize()).isEqualTo(productValue.getSize());
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
				true, null, ProductStatus.AVAILABLE, testTime, testTime, null
			);
			given(productRepository.save(any(Product.class))).willReturn(entity);

			when(productRepository.findById(productId)).thenReturn(Optional.of(entity));

			entity.toModifyStatus(newStatus);

			ProductManagementDto result = productManagementService.modifyToStatus(productId, newStatus);

			assertThat(result.getStatus()).isEqualTo(newStatus);
		}
	}

	@Nested
	class 상품_재고_변경 {
		@Test
		void 상품_재고_변경_성공() {

			final Integer productId = 1;

			final Integer stock = 20;

			final Integer existStock = 50;

			ProductManagementDto.Request.Stock request = new ProductManagementDto.Request.Stock(productId, stock);

			final Product entity = new Product(
				productId, ProductCategory.BEAN, 1000, existStock, seller, 0, false,
				"정말 맛있는 원두 단돈 천원", Bean.ARABICA, Acidity.CINNAMON, "부산 진구 유명가수가 좋아하는 원두",
				true, null, ProductStatus.AVAILABLE, testTime, testTime, null
			);

			given(productRepository.save(any(Product.class))).willReturn(entity);

			when(productRepository.findById(productId)).thenReturn(Optional.of(entity));

			ProductManagementDto result = productManagementService.increaseToStock(request);

			assertThat(result.getStock()).isEqualTo(existStock + stock);
		}

		@Test
		void 상품_재고_변경_실패() {

			final Integer productId = 1;

			final Integer stock = 40;

			final Integer existStock = 30;

			ProductManagementDto.Request.Stock request = new ProductManagementDto.Request.Stock(productId, stock);

			final Product entity = new Product(
				productId, ProductCategory.BEAN, 1000, existStock, seller, 0, false,
				"정말 맛있는 원두 단돈 천원", Bean.ARABICA, Acidity.CINNAMON, "부산 진구 유명가수가 좋아하는 원두",
				true, null, ProductStatus.AVAILABLE, testTime, testTime, null
			);

			given(productRepository.save(any(Product.class))).willReturn(entity);

			when(productRepository.findById(productId)).thenReturn(Optional.of(entity));

			assertThatThrownBy(() -> productManagementService.decreaseToStock(request))
				.isInstanceOf(CustomException.class)
				.hasMessage(ProductManagementErrorCode.CAN_NOT_BE_SET_TO_BELOW_ZERO.getMessage());
		}
	}

	@Test
	void 상품_수정() {
		final Integer productId = 1;

		final ProductManagementDto.Request.Modify dto = new ProductManagementDto.Request.Modify(
			true, 10000, Acidity.CINNAMON, Bean.ARABICA, ProductCategory.BEAN,
			"수정된", "커피", true);

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
			true, null, ProductStatus.AVAILABLE, testTime, testTime, mockImages
		);

		entity.getImages().add(image);

		when(productRepository.findById(productId)).thenReturn(Optional.of(entity));

		final MockMultipartFile mockThumbnailImage = new MockMultipartFile("thumbnailImage", "test.txt",
			"multipart/form-data",
			"test file".getBytes(StandardCharsets.UTF_8));

		List<MultipartFile> mockMultipartFiles = new ArrayList<>();

		final MockMultipartFile mockMultipartFile = new MockMultipartFile("images", "test2.txt", "multipart/form-data",
			"test file2".getBytes(StandardCharsets.UTF_8));

		mockMultipartFiles.add(mockMultipartFile);

		ProductManagementDto resultDto = productManagementService.modifyToProduct(productId, dto, mockThumbnailImage,
			mockMultipartFiles);

		verify(productRepository).findById(productId);

		assertThat(resultDto.getAcidity()).isEqualTo(dto.acidity());
		assertThat(resultDto.getBean()).isEqualTo(dto.bean());
		assertThat(resultDto.getCategory()).isEqualTo(dto.category());
		assertThat(resultDto.getInformation()).isEqualTo(dto.information());
		assertThat(resultDto.getName()).isEqualTo(dto.name());
		assertThat(resultDto.getPrice()).isEqualTo(dto.price());
		assertThat(resultDto.getIsCrush()).isEqualTo(dto.isCrush());
		assertThat(resultDto.getIsDecaf()).isEqualTo(dto.isDecaf());
	}
}
