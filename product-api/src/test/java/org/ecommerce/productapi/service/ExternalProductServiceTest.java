package org.ecommerce.productapi.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.productapi.dto.ImageDto;
import org.ecommerce.productapi.dto.ProductDetailDto;
import org.ecommerce.productapi.dto.ProductWithSellerRepAndImagesAndProductDetailsDto;
import org.ecommerce.productapi.dto.request.CreateProductRequest;
import org.ecommerce.productapi.dto.request.ModifyProductDetailRequest;
import org.ecommerce.productapi.dto.request.ModifyProductRequest;
import org.ecommerce.productapi.dto.request.ModifyProductsStatusRequest;
import org.ecommerce.productapi.dto.request.ModifyStockRequest;
import org.ecommerce.productapi.entity.Image;
import org.ecommerce.productapi.entity.Product;
import org.ecommerce.productapi.entity.ProductDetail;
import org.ecommerce.productapi.entity.SellerRep;
import org.ecommerce.productapi.entity.enumerated.Acidity;
import org.ecommerce.productapi.entity.enumerated.Bean;
import org.ecommerce.productapi.entity.enumerated.ProductCategory;
import org.ecommerce.productapi.entity.enumerated.ProductStatus;
import org.ecommerce.productapi.exception.ProductErrorCode;
import org.ecommerce.productapi.external.service.ProductService;
import org.ecommerce.productapi.provider.S3Provider;
import org.ecommerce.productapi.repository.ProductDetailRepository;
import org.ecommerce.productapi.repository.ProductRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class ExternalProductServiceTest {

	private static final LocalDateTime testTime = LocalDateTime.parse("2024-04-14T17:41:52+09:00",
		DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"));
	private static final SellerRep seller = new SellerRep(1, "TEST");

	@InjectMocks
	private ProductService productService;

	@Mock
	private ProductRepository productRepository;

	@Mock
	private ProductDetailRepository productDetailRepository;

	@Mock
	private S3Provider s3Provider;

	@Nested
	class 상품_등록 {
		@Test
		void 상품_등록_성공() {
			// Arrange
			final List<ImageDto> imageDtos = List.of(
				new ImageDto("image1.jpg", (short)1, true),
				new ImageDto("image2.jpg", (short)2, false),
				new ImageDto("image3.jpg", (short)3, false)
			);

			final CreateProductRequest productRequest = new CreateProductRequest(
				false,
				Acidity.CINNAMON,
				Bean.ARABICA,
				ProductCategory.BEAN,
				"정말 맛있는 원두 단돈 천원",
				"부산 진구 유명가수가 좋아하는 원두",
				false,
				null,
				(short)1000,
				List.of(new ProductDetailDto(1000, 50, "Small", true, ProductStatus.AVAILABLE))
			);

			final Product product = Product.createProduct(
				productRequest.category(),
				productRequest.name(),
				productRequest.bean(),
				productRequest.acidity(),
				productRequest.information(),
				productRequest.isCrush(),
				productRequest.isDecaf(),
				productRequest.capacity(),
				productRequest.deliveryFee(),
				seller
			);

			given(productRepository.save(any(Product.class))).willReturn(product);
			final ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);

			final MockMultipartFile mockThumbnailImage = new MockMultipartFile("thumbnailImage", "test.txt",
				"multipart/form-data", "test file".getBytes(StandardCharsets.UTF_8));

			List<MultipartFile> mockMultipartFiles = new ArrayList<>();
			final MockMultipartFile mockMultipartFile = new MockMultipartFile("images", "test2.txt",
				"multipart/form-data", "test file2".getBytes(StandardCharsets.UTF_8));
			mockMultipartFiles.add(mockMultipartFile);

			when(s3Provider.uploadImageFiles(mockThumbnailImage, mockMultipartFiles)).thenReturn(imageDtos);

			// Act
			final ProductWithSellerRepAndImagesAndProductDetailsDto productDto = productService.productRegister(
				productRequest,
				mockThumbnailImage,
				mockMultipartFiles);

			// Assert
			verify(productRepository, times(1)).save(captor.capture());
			Product productValue = captor.getValue();
			assertThat(productDto.acidity()).isEqualTo(productValue.getAcidity());
			assertThat(productDto.bean()).isEqualTo(productValue.getBean());
			assertThat(productDto.category()).isEqualTo(productValue.getCategory());
			assertThat(productDto.information()).isEqualTo(productValue.getInformation());
			assertThat(productDto.name()).isEqualTo(productValue.getName());
			assertThat(productDto.sellerRep()).usingRecursiveComparison().isEqualTo(productValue.getSellerRep());
			assertThat(productDto.isCrush()).isEqualTo(productValue.getIsCrush());
			assertThat(productDto.isDecaf()).isEqualTo(productValue.getIsDecaf());
		}

	}

	@Nested
	class 상품_상태_수정 {
		@Test
		void 상품_상태_수정_성공() {
			// Arrange
			final Integer productId = 1;
			final ProductStatus newStatus = ProductStatus.DISCONTINUED;

			final Product product = new Product(
				productId,
				ProductCategory.BEAN,
				seller,
				0,
				false,
				"부산 진구 유명가수가 좋아하는 원두",
				Bean.ARABICA,
				Acidity.CINNAMON,
				"정말 맛있는 원두 단돈 천원",
				false,
				null,
				testTime,
				testTime,
				(short)1000,
				List.of(ProductDetail.ofCreate(null, 1000, 50, "500g", true, ProductStatus.AVAILABLE)),
				List.of(Image.ofCreate("wwww.test.test", true, (short)1, null)));

			given(productRepository.findProductWithProductDetailsById(productId)).willReturn(product);

			// Act
			ProductWithSellerRepAndImagesAndProductDetailsDto result = productService.modifyToStatus(productId,
				newStatus);

			// Assert
			assertThat(result.productDetails().get(0).status()).isEqualTo(newStatus);
		}

		@Test
		void 상품_상태_변경_실패_상품을_못찾은_케이스() {
			// Arrange
			final Integer productId = 1;
			final ProductStatus newStatus = ProductStatus.DISCONTINUED;

			given(productRepository.findProductWithProductDetailsById(productId)).willReturn(null);

			// Act & Assert
			assertThatThrownBy(() -> productService.modifyToStatus(productId, newStatus))
				.isInstanceOf(CustomException.class)
				.hasMessage(ProductErrorCode.NOT_FOUND_PRODUCT.getMessage());
		}

		@Test
		void 여러개_상품_상태_수정_성공() {
			// Arrange
			final List<Integer> productIds = List.of(1, 2);
			final ProductStatus newStatus = ProductStatus.DISCONTINUED;

			final Product product1 = new Product(
				productIds.get(0),
				ProductCategory.BEAN,
				seller,
				0,
				false,
				"부산 진구 유명가수가 좋아하는 원두",
				Bean.ARABICA,
				Acidity.CINNAMON,
				"정말 맛있는 원두 단돈 천원",
				false,
				null,
				testTime,
				testTime,
				(short)1000,
				List.of(ProductDetail.ofCreate(null, 1000, 50, "500g", true, ProductStatus.AVAILABLE)),
				List.of(Image.ofCreate("wwww.test.test", true, (short)1, null)));

			final Product product2 = new Product(
				productIds.get(1),
				ProductCategory.BEAN,
				seller,
				0,
				false,
				"부산 진구 유명가수가 좋아하는 원두",
				Bean.ARABICA,
				Acidity.CINNAMON,
				"정말 맛있는 원두 단돈 천원",
				false,
				null,
				testTime,
				testTime,
				(short)1000,
				List.of(ProductDetail.ofCreate(null, 1000, 50, "500g", true, ProductStatus.AVAILABLE)),
				List.of(Image.ofCreate("wwww.test.test", true, (short)1, null)));

			List<Product> products = List.of(product1, product2);

			given(productRepository.findProductWithProductDetailsByIds(productIds)).willReturn(products);

			ModifyProductsStatusRequest request = new ModifyProductsStatusRequest(productIds, newStatus);

			// Act
			List<ProductWithSellerRepAndImagesAndProductDetailsDto> result = productService.bulkModifyStatus(request);

			// Assert
			assertThat(result).hasSize(2);
			assertThat(result.get(0).productDetails().get(0).status()).isEqualTo(newStatus);
			assertThat(result.get(1).productDetails().get(0).status()).isEqualTo(newStatus);
		}

		@Test
		void 상품_디테일_상태_변경_성공() {
			final Integer productDetailId = 1;
			final ProductStatus requestStatus = ProductStatus.OUT_OF_STOCK;

			final ProductDetail productDetail = new ProductDetail(
				1, null, 1000, 50, "500g", true,
				ProductStatus.AVAILABLE);

			given(productDetailRepository.findByProductDetailId(productDetailId)).willReturn(productDetail);

			ProductDetailDto result = productService.modifyToProductDetailStatus(productDetailId,
				requestStatus);

			assertThat(result.status()).isEqualTo(requestStatus);
		}
	}

	@Nested
	class 재고_테스트 {
		@Test
		void 재고_증가_성공() {
			// Arrange
			final Integer productId = 1;
			final ModifyStockRequest request = new ModifyStockRequest(1, 100);

			ProductDetail productDetail = new ProductDetail(1, null, 1000, 50, "Small", true, ProductStatus.AVAILABLE);

			given(productDetailRepository.findByProductDetailId(productId)).willReturn(productDetail);

			// Act
			ProductDetailDto result = productService.increaseToStock(request);

			// Assert
			assertThat(result.stock()).isEqualTo(150);
		}

		@Test
		void 재고_감소_성공() {
			// Arrange
			final Integer productId = 1;
			final ModifyStockRequest request = new ModifyStockRequest(1, 40);

			int existStock = 50;
			ProductDetail productDetail = new ProductDetail(1, null, 1000, existStock, "Small", true,
				ProductStatus.AVAILABLE);

			given(productDetailRepository.findByProductDetailId(productId)).willReturn(productDetail);

			ProductDetailDto result = productService.decreaseToStock(request);

			// Act & Assert
			assertThat(result.stock()).isEqualTo(existStock - request.requestStock());
		}

		@Test
		void 재고_증가_실패_상품_못찾은_케이스() {
			// Arrange
			final Integer productId = 1;
			final ModifyStockRequest request = new ModifyStockRequest(1, 100);

			given(productDetailRepository.findByProductDetailId(productId)).willReturn(null);

			// Act & Assert
			assertThatThrownBy(() -> productService.increaseToStock(request))
				.isInstanceOf(CustomException.class)
				.hasMessage(ProductErrorCode.NOT_FOUND_PRODUCT.getMessage());
		}

		@Test
		void 재고_감소_실패_0미만으로_설정한_경우() {
			// Arrange
			final Integer productId = 1;
			final ModifyStockRequest request = new ModifyStockRequest(1, 100);

			ProductDetail productDetail = new ProductDetail(1, null, 1000, 50, "Small", true, ProductStatus.AVAILABLE);

			given(productDetailRepository.findByProductDetailId(productId)).willReturn(productDetail);

			// Act & Assert
			assertThatThrownBy(() -> productService.decreaseToStock(request))
				.isInstanceOf(CustomException.class)
				.hasMessage(ProductErrorCode.CAN_NOT_BE_SET_TO_BELOW_ZERO.getMessage());
		}
	}

	@Nested
	class 상품_수정 {
		@Test
		void 성공() {
			// Arrange
			final List<ImageDto> imageDtos = List.of(
				new ImageDto("image1.jpg", (short)1, true),
				new ImageDto("image2.jpg", (short)2, false),
				new ImageDto("image3.jpg", (short)3, false)
			);

			final Integer productId = 1;
			final ModifyProductRequest modifyProductRequest = new ModifyProductRequest(
				false, Acidity.CINNAMON, Bean.ARABICA, ProductCategory.BEAN, "수정된", "커피", null, true, (short)4000
			);
			final Image image = Image.ofCreate(
				"test",
				true,
				(short)1,
				null
			);

			final List<Image> mockImages = new ArrayList<>();

			mockImages.add(image);

			final Product product = new Product(
				productId,
				ProductCategory.BEAN,
				seller,
				0,
				false,
				"부산 진구 유명가수가 좋아하는 원두",
				Bean.ARABICA,
				Acidity.CINNAMON,
				"정말 맛있는 원두 단돈 천원",
				false,
				null,
				testTime,
				testTime,
				(short)1000,
				List.of(ProductDetail.ofCreate(null, 1000, 50, "500g", true, ProductStatus.AVAILABLE)),
				mockImages);

			given(productRepository.findProductWithProductDetailsById(productId)).willReturn(product);

			final MockMultipartFile mockThumbnailImage = new MockMultipartFile("thumbnailImage", "test.txt",
				"multipart/form-data", "test file".getBytes(StandardCharsets.UTF_8));
			List<MultipartFile> mockMultipartFiles = List.of(
				new MockMultipartFile("images", "test2.txt", "multipart/form-data",
					"test file2".getBytes(StandardCharsets.UTF_8))
			);

			when(s3Provider.uploadImageFiles(mockThumbnailImage, mockMultipartFiles)).thenReturn(imageDtos);

			ProductWithSellerRepAndImagesAndProductDetailsDto resultDto = productService.modifyToProduct(
				productId, modifyProductRequest, mockThumbnailImage, mockMultipartFiles);

			verify(productRepository).findProductWithProductDetailsById(productId);
			assertThat(resultDto.acidity()).isEqualTo(modifyProductRequest.acidity());
			assertThat(resultDto.bean()).isEqualTo(modifyProductRequest.bean());
			assertThat(resultDto.category()).isEqualTo(modifyProductRequest.category());
			assertThat(resultDto.information()).isEqualTo(modifyProductRequest.information());
			assertThat(resultDto.name()).isEqualTo(modifyProductRequest.name());
			assertThat(resultDto.deliveryFee()).isEqualTo(modifyProductRequest.deliveryFee());
			assertThat(resultDto.isCrush()).isEqualTo(modifyProductRequest.isCrush());
			assertThat(resultDto.isDecaf()).isEqualTo(modifyProductRequest.isDecaf());
		}

		@Test
		void 실패_상품을_못찾은_케이스() {
			// Arrange
			final Integer productId = 1;
			final ModifyProductRequest modifyProductRequest = new ModifyProductRequest(
				false, Acidity.CINNAMON, Bean.ARABICA, ProductCategory.BEAN, "수정된", "커피", null, true, (short)4000
			);

			final Image image = Image.ofCreate(
				"test",
				true,
				(short)1,
				null
			);

			List<Image> mockImages = new ArrayList<>();

			mockImages.add(image);

			final Product product = new Product(
				productId,
				ProductCategory.BEAN,
				seller,
				0,
				false,
				"부산 진구 유명가수가 좋아하는 원두",
				Bean.ARABICA,
				Acidity.CINNAMON,
				"정말 맛있는 원두 단돈 천원",
				false,
				null,
				testTime,
				testTime,
				(short)1000,
				List.of(ProductDetail.ofCreate(null, 1000, 50, "500g", true, ProductStatus.AVAILABLE)),
				mockImages);

			final MockMultipartFile mockThumbnailImage = new MockMultipartFile("thumbnailImage", "test.txt",
				"multipart/form-data", "test file".getBytes(StandardCharsets.UTF_8));
			List<MultipartFile> mockMultipartFiles = List.of(
				new MockMultipartFile("images", "test2.txt", "multipart/form-data",
					"test file2".getBytes(StandardCharsets.UTF_8))
			);
			given(productRepository.findProductWithProductDetailsById(productId)).willReturn(null);

			// Act & Assert
			assertThatThrownBy(() -> productService.modifyToProduct(
				productId, modifyProductRequest, mockThumbnailImage, mockMultipartFiles))
				.isInstanceOf(CustomException.class)
				.hasMessage(ProductErrorCode.NOT_FOUND_PRODUCT.getMessage());
		}

		@Test
		void 디테일_수정_성공() {
			final int productDetailId = 1;

			final Image image = Image.ofCreate(
				"test",
				true,
				(short)1,
				null
			);

			List<Image> mockImages = new ArrayList<>();

			mockImages.add(image);

			int productId = 1;
			final Product product = new Product(
				productId,
				ProductCategory.BEAN,
				seller,
				0,
				false,
				"부산 진구 유명가수가 좋아하는 원두",
				Bean.ARABICA,
				Acidity.CINNAMON,
				"정말 맛있는 원두 단돈 천원",
				false,
				null,
				testTime,
				testTime,
				(short)1000,
				List.of(ProductDetail.ofCreate(null, 1000, 50, "500g", true, ProductStatus.AVAILABLE)),
				mockImages);

			final ProductDetail productDetail = new ProductDetail(productDetailId, product, 1000, 50, "500g", false,
				ProductStatus.AVAILABLE);

			final ModifyProductDetailRequest request = new ModifyProductDetailRequest(3000, "500g", true);

			given(productDetailRepository.findByProductDetailId(productDetailId)).willReturn(productDetail);
			given(productRepository.findProductWithProductDetailsById(productId)).willReturn(product);

			ProductDetailDto result = productService.modifyToProductDetail(productDetailId, request);

			assertThat(result.price()).isEqualTo(request.price());
			assertThat(result.size()).isEqualTo(request.size());
			assertThat(result.isDefault()).isEqualTo(request.isDefault());
		}
	}

	@Nested
	class 상품_삭제 {
		@Test
		void 상품_삭제_성공() {
			final int productDetailId1 = 1;
			final int productDetailId2 = 2;

			final Image image = Image.ofCreate(
				"test",
				true,
				(short)1,
				null
			);

			List<Image> mockImages = new ArrayList<>();
			mockImages.add(image);

			int productId = 1;
			final Product product = new Product(
				productId,
				ProductCategory.BEAN,
				seller,
				0,
				false,
				"부산 진구 유명가수가 좋아하는 원두",
				Bean.ARABICA,
				Acidity.CINNAMON,
				"정말 맛있는 원두 단돈 천원",
				false,
				null,
				testTime,
				testTime,
				(short)1000,
				new ArrayList<>(List.of(ProductDetail.ofCreate(null, 1000, 50, "500g", true, ProductStatus.AVAILABLE),
					ProductDetail.ofCreate(null, 1000, 50, "700g", true, ProductStatus.AVAILABLE))),
				mockImages
			);

			final ProductDetail productDetail1 = new ProductDetail(productDetailId1, product, 1000, 50, "500g", false,
				ProductStatus.AVAILABLE);
			final ProductDetail productDetail2 = new ProductDetail(productDetailId2, product, 1000, 50, "700g", false,
				ProductStatus.AVAILABLE);

			final Integer request = productDetailId1;

			given(productDetailRepository.findByProductDetailId(productDetailId1)).willReturn(productDetail1);
			given(productRepository.findProductWithProductDetailsById(productId)).willReturn(product);

			String response = productService.deleteProductDetail(request);

			assertThat(response).isEqualTo("상품 디테일 삭제를 성공 하였습니다");
		}

		@Test
		void 상품_삭제_실패_상품디테일이_하나도_안남은_경우() {
			final int productDetailId1 = 1;

			final Image image = Image.ofCreate(
				"test",
				true,
				(short)1,
				null
			);

			List<Image> mockImages = new ArrayList<>();
			mockImages.add(image);

			int productId = 1;
			final Product product = new Product(
				productId,
				ProductCategory.BEAN,
				seller,
				0,
				false,
				"부산 진구 유명가수가 좋아하는 원두",
				Bean.ARABICA,
				Acidity.CINNAMON,
				"정말 맛있는 원두 단돈 천원",
				false,
				null,
				testTime,
				testTime,
				(short)1000,
				new ArrayList<>(List.of(ProductDetail.ofCreate(null, 1000, 50, "500g", true, ProductStatus.AVAILABLE))),
				mockImages
			);

			final ProductDetail productDetail1 = new ProductDetail(productDetailId1, product, 1000, 50, "500g", false,
				ProductStatus.AVAILABLE);

			final Integer request = productDetailId1;

			given(productDetailRepository.findByProductDetailId(productDetailId1)).willReturn(productDetail1);
			given(productRepository.findProductWithProductDetailsById(productId)).willReturn(product);

			assertThatThrownBy(() -> productService.deleteProductDetail(request))
				.isInstanceOf(CustomException.class)
				.hasMessage(ProductErrorCode.IS_NOT_ENOUGH_PRODUCT_DETAIL.getMessage());
		}
	}
}
