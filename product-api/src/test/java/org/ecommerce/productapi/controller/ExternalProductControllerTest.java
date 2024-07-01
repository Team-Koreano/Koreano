package org.ecommerce.productapi.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.ecommerce.productapi.ControllerTest;
import org.ecommerce.productapi.config.MockS3Config;
import org.ecommerce.productapi.dto.ImageDto;
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
import org.ecommerce.productapi.entity.enumerated.Acidity;
import org.ecommerce.productapi.entity.enumerated.Bean;
import org.ecommerce.productapi.entity.enumerated.ProductCategory;
import org.ecommerce.productapi.entity.enumerated.ProductStatus;
import org.ecommerce.productapi.external.controller.ProductController;
import org.ecommerce.productapi.external.service.ElasticSearchService;
import org.ecommerce.productapi.external.service.ProductService;
import org.ecommerce.productapi.provider.S3Provider;
import org.ecommerce.productapi.repository.ImageRepository;
import org.ecommerce.productapi.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

@Import(MockS3Config.class)
@WebMvcTest(ProductController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class ExternalProductControllerTest extends ControllerTest {

	private static final SellerRep testSeller = new SellerRep(1, "TEST");
	private static final LocalDateTime testTime = LocalDateTime.parse(
		"2024-04-14T17:41:52+09:00",
		DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")
	);

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private WebApplicationContext context;

	@MockBean
	private ProductRepository productRepository;

	@MockBean
	private ProductService productService;

	@MockBean
	private S3Provider s3Provider;

	@MockBean
	private ElasticSearchService elasticSearchService;

	@MockBean
	private ImageRepository imageRepository;

	@BeforeEach
	void setUp() {
		this.mockMvc = MockMvcBuilders
			.webAppContextSetup(context)
			.addFilters(new CharacterEncodingFilter(StandardCharsets.UTF_8.name(), true))
			.build();
	}

	@Test
	void 상품_등록() throws Exception {
		// Given
		final List<ImageDto> imageDtos = List.of(
			new ImageDto("image1.jpg", (short)1, true),
			new ImageDto("image2.jpg", (short)2, false),
			new ImageDto("image3.jpg", (short)3, false)
		);

		final CreateProductRequest productRequest = new CreateProductRequest(
			true, Acidity.CINNAMON, Bean.ARABICA, ProductCategory.BEAN, "부산 커피 단돈 5000천원", "맛있는 커피", true, null,
			(short)3000,
			List.of(new ProductDetailDto(1, 5000, 20, "500g", true, ProductStatus.AVAILABLE))
		);

		final Product product = new Product(
			1,
			productRequest.category(),
			testSeller,
			0,
			productRequest.isDecaf(),
			productRequest.name(),
			productRequest.bean(),
			productRequest.acidity(),
			productRequest.information(),
			productRequest.isCrush(),
			productRequest.capacity(),
			testTime,
			testTime,
			productRequest.deliveryFee(),
			List.of(new ProductDetail(1, null, 5000, 20, "500g", true, ProductStatus.AVAILABLE)),
			List.of()
		);

		final MockMultipartFile mockThumbnailImage = createMockFile("thumbnailImage");

		List<MultipartFile> mockMultipartFiles = List.of(createMockFile("images"));

		final MockMultipartFile productJson = new MockMultipartFile(
			"product", "", "application/json",
			objectMapper.writeValueAsString(productRequest).getBytes(StandardCharsets.UTF_8)
		);

		ProductWithSellerRepAndImagesAndProductDetailsDto expectedResponse = ProductMapper.INSTANCE.toDto(product);

		saveImages(imageDtos, product);

		when(productService.productRegister(
				eq(productRequest),
				eq(mockThumbnailImage),
				any(),
				eq(mockMultipartFiles)
			)
		)
			.thenReturn(expectedResponse);

		when(s3Provider.uploadImageFiles(
				mockThumbnailImage,
				mockMultipartFiles
			)
		)
			.thenReturn(imageDtos);

		// When & Then
		mockMvc.perform(multipart("/api/external/product/v1")
				.file(productJson)
				.file(mockThumbnailImage)
				.file((MockMultipartFile)mockMultipartFiles.get(0))
				.contentType(MediaType.MULTIPART_FORM_DATA)
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.result.name").value(product.getName()))
			.andExpect(
				jsonPath("$.result.productDetails[0].price").value(product.getProductDetails().get(0).getPrice()))
			.andExpect(
				jsonPath("$.result.productDetails[0].stock").value(product.getProductDetails().get(0).getStock()))
			.andExpect(jsonPath("$.result.categoryResponse.acidity").value(product.getAcidity().getCode()))
			.andExpect(jsonPath("$.result.categoryResponse.bean").value(product.getBean().getCode()))
			.andExpect(jsonPath("$.result.category").value(product.getCategory().getCode()))
			.andExpect(jsonPath("$.result.information").value(product.getInformation()))
			.andExpect(jsonPath("$.result.productDetails[0].status").value(
				product.getProductDetails().get(0).getStatus().getCode()))
			.andExpect(jsonPath("$.result.categoryResponse.isCrush").value(product.getIsCrush()))
			.andExpect(jsonPath("$.result.bizName").value(product.getSellerRep().getBizName()))
			.andDo(print());
	}

	@Test
	void 상품_등록_잘못된_요청() throws Exception {
		// Given
		final CreateProductRequest invalidProductRequest = new CreateProductRequest(
			null, null, null, null, "", "", false, null, (short)0, List.of()
		);

		final MockMultipartFile productJson = new MockMultipartFile(
			"product", "", "application/json",
			objectMapper.writeValueAsString(invalidProductRequest).getBytes(StandardCharsets.UTF_8)
		);

		// When & Then
		mockMvc.perform(multipart("/api/external/product/v1")
				.file(productJson)
				.contentType(MediaType.MULTIPART_FORM_DATA)
			)
			.andExpect(status().isBadRequest());
	}

	void 상품_등록_잘못된_파일_형식() throws Exception {
		// Given
		final CreateProductRequest productRequest = new CreateProductRequest(
			true, Acidity.CINNAMON, Bean.ARABICA, ProductCategory.BEAN, "부산 커피 단돈 5000천원", "맛있는 커피", true, null,
			(short)3000,
			List.of(new ProductDetailDto(1, 5000, 20, "500g", true, ProductStatus.AVAILABLE))
		);

		final MockMultipartFile productJson = new MockMultipartFile(
			"product", "", "application/json",
			objectMapper.writeValueAsString(productRequest).getBytes(StandardCharsets.UTF_8)
		);

		final MockMultipartFile invalidFile = new MockMultipartFile(
			"images", "test.exe", "application/octet-stream", "test file".getBytes(StandardCharsets.UTF_8)
		);

		// When & Then
		mockMvc.perform(multipart("/api/external/product/v1")
				.file(productJson)
				.file(invalidFile)
				.contentType(MediaType.MULTIPART_FORM_DATA)
			)
			.andExpect(status().isBadRequest());
	}

	@Test
	void 상품_상태_변경() throws Exception {
		// Given
		final int productId = 1;
		final ProductStatus status = ProductStatus.DISCONTINUED;

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
			testSeller,
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

		product.toModifyStatus(status);

		ProductWithSellerRepAndImagesAndProductDetailsDto expectedResponse = ProductMapper.INSTANCE.toDto(product);

		when(productService.modifyToStatus(eq(productId), eq(status), any())).thenReturn(
			expectedResponse);

		// When & Then
		ResultActions perform = mockMvc.perform(
			put("/api/external/product/v1/{productId}/{status}", productId, status)
				.contentType(MediaType.APPLICATION_JSON));
		perform
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.result.id").value(productId))
			.andExpect(jsonPath("$.result.productDetails[0].status").value(status.getCode()));
	}

	@Test
	void 상품_재고_수정() throws Exception {
		// Given
		final int productDetailId = 1;
		final int changedStock = 10;
		final Integer productId = 1;

		final ModifyStockRequest request = new ModifyStockRequest(productDetailId, changedStock);

		ProductDetail productDetail = new ProductDetail(productDetailId, null, 1000, 50, "500g", true,
			ProductStatus.AVAILABLE);

		final Product product = new Product(
			productId,
			ProductCategory.BEAN,
			testSeller,
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
			List.of(productDetail),
			List.of()
		);

		ProductWithSellerRepAndImagesAndProductDetailsDto expectedResponse = ProductMapper.INSTANCE.toDto(product);

		when(productService.increaseToStock(eq(productId), eq(request), any())).thenReturn(expectedResponse);

		// When & Then
		mockMvc.perform(put("/api/external/product/v1/detail/{productId}/stock/increase", productId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.result.productDetails[0].stock").value(productDetail.getStock()));
	}

	@Test
	void 상품_수정() throws Exception {
		// Given
		final int productId = 1;
		final ModifyProductRequest request = new ModifyProductRequest(
			false, Acidity.CINNAMON, Bean.ARABICA, ProductCategory.BEAN, "수정된", "커피", null, true, (short)4000
		);

		final Product product = new Product(
			productId,
			ProductCategory.BEAN,
			testSeller,
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
			List.of()
		);

		final MockMultipartFile mockThumbnailImage = createMockFile("thumbnailImage");

		List<MultipartFile> mockMultipartFiles = List.of(createMockFile("images"));

		final MockMultipartFile productJson = new MockMultipartFile(
			"modifyProduct", "", "application/json",
			objectMapper.writeValueAsString(request).getBytes(StandardCharsets.UTF_8)
		);

		ProductWithSellerRepAndImagesAndProductDetailsDto expectedResponse = ProductMapper.INSTANCE.toDto(product);

		when(productService.modifyToProduct(
				eq(productId),
				eq(request),
				eq(mockThumbnailImage),
				eq(mockMultipartFiles),
				any()
			)
		)
			.thenReturn(expectedResponse);

		// When & Then
		mockMvc.perform(
				multipart("/api/external/product/v1/{productId}", productId)
					.file(productJson)
					.file(mockThumbnailImage)
					.file((MockMultipartFile)mockMultipartFiles.get(0))
					.with(new RequestPostProcessor() {
						@Override
						public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
							request.setMethod("PUT");
							return request;
						}
					})
					.contentType(MediaType.MULTIPART_FORM_DATA))
			.andExpect(status().isOk())
			.andDo(print())
			.andExpect(jsonPath("$.result.id").value(expectedResponse.id()))
			.andExpect(jsonPath("$.result.category").value(expectedResponse.category().name()))
			.andExpect(jsonPath("$.result.name").value(expectedResponse.name()))
			.andExpect(jsonPath("$.result.information").value(expectedResponse.information()))
			.andExpect(jsonPath("$.result.categoryResponse.isCrush").value(expectedResponse.isCrush()))
			.andExpect(jsonPath("$.result.categoryResponse.isDecaf").value(expectedResponse.isDecaf()));
	}

	@Test
	void 여러개_상품_상태_변경() throws Exception {
		// Given
		final List<Integer> productIds = List.of(1, 2);
		final ProductStatus status = ProductStatus.DISCONTINUED;
		final ModifyProductsStatusRequest request = new ModifyProductsStatusRequest(productIds, status);

		final Product product1 = new Product(
			productIds.get(0),
			ProductCategory.BEAN,
			testSeller,
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
			testSeller,
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

		final List<Product> products = List.of(product1, product2);

		products.forEach(product -> product.toModifyStatus(status));

		final List<ProductWithSellerRepAndImagesAndProductDetailsDto> expectedResponse = ProductMapper.INSTANCE.toDtos(
			products);

		when(productService.bulkModifyStatus(eq(request), any())).thenReturn(expectedResponse);

		// When & Then
		mockMvc.perform(put("/api/external/product/v1/status")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andDo(print())
			.andExpect(jsonPath("$.result[0].id").value(product1.getId()))
			.andExpect(jsonPath("$.result[0].productDetails[0].status").value(status.name()))
			.andExpect(jsonPath("$.result[1].id").value(product2.getId()))
			.andExpect(jsonPath("$.result[1].productDetails[0].status").value(status.name()));
	}

	@Test
	void 상품_상세_추가() throws Exception {
		// Given
		final int productId = 1;
		final AddProductDetailRequest request = new AddProductDetailRequest(6000, 30, "1kg", true,
			ProductStatus.AVAILABLE);

		final ProductDetail productDetail = new ProductDetail(2, null, 6000, 30, "1kg", true, ProductStatus.AVAILABLE);
		final ProductDetailDto expectedResponse = ProductMapper.INSTANCE.toDto(productDetail);

		when(productService.addProductDetail(eq(productId), eq(request), any())).thenReturn(
			expectedResponse);

		// When & Then
		mockMvc.perform(post("/api/external/product/v1/detail/{productId}", productId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.result.price").value(productDetail.getPrice()))
			.andExpect(jsonPath("$.result.stock").value(productDetail.getStock()))
			.andExpect(jsonPath("$.result.status").value(productDetail.getStatus().name()));
	}

	@Test
	void 상품_상세_수정() throws Exception {
		// Given
		final int productDetailId = 1;
		final int productId = 1;
		final ModifyProductDetailRequest request = new ModifyProductDetailRequest(7000, "750g", true);

		final ProductDetail productDetail = new ProductDetail(productDetailId, null, 7000, 25, "750g", false,
			ProductStatus.AVAILABLE);

		final Product product = new Product(
			productId,
			ProductCategory.BEAN,
			testSeller,
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
			List.of(productDetail),
			List.of()
		);

		final ProductWithSellerRepAndImagesAndProductDetailsDto expectedResponse =
			ProductMapper.INSTANCE.toDto(product);

		when(productService.modifyToProductDetail(
				eq(productId),
				eq(productDetailId),
				eq(request),
				any()
			)
		)
			.thenReturn(
				expectedResponse
			);

		// When & Then
		mockMvc.perform(put("/api/external/product/v1/detail/{productId}/{productDetailId}", productId, productDetailId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.result.productDetails[0].price").value(productDetail.getPrice()))
			.andExpect(jsonPath("$.result.productDetails[0].stock").value(productDetail.getStock()))
			.andExpect(jsonPath("$.result.productDetails[0].isDefault").value(productDetail.getIsDefault()));
	}

	@Test
	void 상품_상세_상태_변경() throws Exception {
		// Given
		final int productId = 1;
		final int productDetailId = 1;
		final ProductStatus status = ProductStatus.OUT_OF_STOCK;
		final ProductDetail productDetail = new ProductDetail(productDetailId, null, 5000, 0, "500g", false, status);

		final Product product = new Product(
			productId,
			ProductCategory.BEAN,
			testSeller,
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
			List.of(productDetail),
			List.of()
		);

		final ProductWithSellerRepAndImagesAndProductDetailsDto expectedResponse =
			ProductMapper.INSTANCE.toDto(product);

		when(productService.modifyToProductDetailStatus(
				eq(productId),
				eq(productDetailId),
				eq(status),
				any()
			)
		).thenReturn(
			expectedResponse
		);

		// When & Then
		mockMvc.perform(
				put(
					"/api/external/product/v1/detail/{productId}/{productDetailId}/{status}",
					productId,
					productDetailId,
					status
				)
					.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.result.productDetails[0].status").value(status.name()));
	}

	@Test
	void 상품_상세_삭제() throws Exception {
		// Given
		final int productId = 1;
		final int productDetailId = 1;
		final String expectedMessage = "상품 디테일 삭제를 성공 하였습니다";

		when(productService.deleteProductDetail(eq(productId), eq(productDetailId), any())).thenReturn(
			expectedMessage);

		// When & Then
		mockMvc.perform(
				delete(
					"/api/external/product/v1/detail/{productId}/{productDetailId}",
					productId,
					productDetailId
				)
					.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.result").value(expectedMessage));
	}

	@Test
	void 상품_재고_감소() throws Exception {
		// Given
		final int productDetailId = 1;
		final int decreasedStock = 5;
		final int productId = 1;

		final ModifyStockRequest request = new ModifyStockRequest(productDetailId, decreasedStock);
		final ProductDetail productDetail = new ProductDetail(productDetailId, null, 1000, 45, "500g", true,
			ProductStatus.AVAILABLE);

		final Product product = new Product(
			productId,
			ProductCategory.BEAN,
			testSeller,
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
			List.of(productDetail),
			List.of()
		);

		final ProductWithSellerRepAndImagesAndProductDetailsDto expectedResponse =
			ProductMapper.INSTANCE.toDto(product);

		when(productService.decreaseToStock(eq(productId), eq(request), any())).thenReturn(expectedResponse);

		// When & Then
		mockMvc.perform(put("/api/external/product/v1/detail/{productId}/stock/decrease", productId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.result.productDetails[0]..stock").value(productDetail.getStock()));
	}

	private void verifyImages(final List<Image> images, final int index, final List<ImageDto> imageDtos) {
		if (index >= images.size()) {
			return;
		}

		final Image image = images.get(index);
		final ImageDto imageDto = imageDtos.get(index);

		assertThat(image.getImageUrl()).isEqualTo(imageDto.imageUrl());
		assertThat(image.getIsThumbnail()).isEqualTo(imageDto.isThumbnail());
		assertThat(image.getSequenceNumber()).isEqualTo(imageDto.sequenceNumber());

		verifyImages(images, index + 1, imageDtos);
	}

	private void saveImages(final List<ImageDto> imageDtos, final Product savedProduct) {
		final List<Image> images = imageDtos.stream()
			.map(imageDto -> Image.ofCreate(imageDto.imageUrl(), imageDto.isThumbnail(), imageDto.sequenceNumber(),
				savedProduct))
			.collect(Collectors.toList());
		imageRepository.saveAll(images);
	}

	private static MockMultipartFile createMockFile(final String name) {
		return new MockMultipartFile(name, "test.txt",
			"multipart/form-data",
			"test file".getBytes(StandardCharsets.UTF_8));
	}
}
