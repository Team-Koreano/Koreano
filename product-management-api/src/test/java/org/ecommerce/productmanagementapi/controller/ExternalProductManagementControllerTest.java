package org.ecommerce.productmanagementapi.controller;

import static org.assertj.core.api.AssertionsForClassTypes.*;
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

import org.ecommerce.product.entity.Image;
import org.ecommerce.product.entity.Product;
import org.ecommerce.product.entity.SellerRep;
import org.ecommerce.product.entity.enumerated.Acidity;
import org.ecommerce.product.entity.enumerated.Bean;
import org.ecommerce.product.entity.enumerated.ProductCategory;
import org.ecommerce.product.entity.enumerated.ProductStatus;
import org.ecommerce.productmanagementapi.config.MockS3Config;
import org.ecommerce.productmanagementapi.dto.ProductManagementDto;
import org.ecommerce.productmanagementapi.dto.ProductManagementMapper;
import org.ecommerce.productmanagementapi.external.ProductManagementService;
import org.ecommerce.productmanagementapi.provider.S3Provider;
import org.ecommerce.productmanagementapi.repository.ImageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@Import(MockS3Config.class)
class ExternalProductManagementControllerTest {

	private static final SellerRep test = new SellerRep(2, "TEST");
	private static final LocalDateTime testTime = LocalDateTime.
		parse("2024-04-14T17:41:52+09:00",
			DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"));
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private WebApplicationContext context;
	@MockBean
	private ProductManagementService productManagementService;
	@MockBean
	private ImageRepository imageRepository;
	@MockBean
	private S3Provider s3Provider;

	@BeforeEach
	void 초기_셋팅() {
		this.mockMvc = MockMvcBuilders
			.webAppContextSetup(context)
			.addFilters(new CharacterEncodingFilter(StandardCharsets.UTF_8.name(), true))  // 필터 추가
			.build();
	}

	@Test
	void 상품_등록() throws Exception {
		//given
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
				false
				, "20 * 50"
				, "500ml"
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
			test
		);
		final MockMultipartFile mockThumbnailImage = createMockFile("thumbnailImage");

		List<MultipartFile> mockMultipartFiles = new ArrayList<>();

		final MockMultipartFile mockMultipartFile = createMockFile("images");
		mockMultipartFiles.add(mockMultipartFile);

		final MockMultipartFile productJson = new MockMultipartFile("product", "",
			"application/json", objectMapper.writeValueAsString(productDtos).getBytes(StandardCharsets.UTF_8));

		final ProductManagementDto productConvertToDto = ProductManagementMapper.INSTANCE.toDto(product);

		saveImages(imageDtos, product);

		when(productManagementService.productRegister(productDtos, mockThumbnailImage,
			mockMultipartFiles)).thenReturn(productConvertToDto);

		when(s3Provider.uploadImageFiles(mockThumbnailImage, mockMultipartFiles)).thenReturn(imageDtos);
		//when
		//then
		final ArgumentCaptor<List<Image>> imageListCaptor = ArgumentCaptor.forClass(List.class);

		verify(imageRepository, times(1)).saveAll(imageListCaptor.capture());

		final List<Image> savedImages = imageListCaptor.getValue();

		verifyImages(savedImages, 0, imageDtos);

		ResultActions resultActions = mockMvc.perform(multipart("/api/external/product/v1")
				.file(productJson)
				.file(mockThumbnailImage)
				.file(mockMultipartFile)
				.contentType(MediaType.MULTIPART_FORM_DATA)
			).andExpect(status().isOk())
			.andDo(print());

		resultActions
			.andExpect(jsonPath("$.result.name").value(product.getName()))
			.andExpect(jsonPath("$.result.price").value(product.getPrice()))
			.andExpect(jsonPath("$.result.stock").value(product.getStock()))
			.andExpect(jsonPath("$.result.acidity").value(product.getAcidity().getCode()))
			.andExpect(jsonPath("$.result.bean").value(product.getBean().getCode()))
			.andExpect(jsonPath("$.result.category").value(product.getCategory().getCode()))
			.andExpect(jsonPath("$.result.information").value(product.getInformation()))
			.andExpect(jsonPath("$.result.status").value(product.getStatus().getCode()))
			.andExpect(jsonPath("$.result.isCrush").value(product.getIsCrush()))
			.andExpect(jsonPath("$.result.bizName").value(product.getSellerRep().getBizName()))
			.andDo(print());
	}

	@Test
	void 상품_상태_변경() throws Exception {
		// Given
		final int productId = 1;
		final ProductStatus status = ProductStatus.DISCONTINUED;

		final Product entity = new Product(
			productId, ProductCategory.BEAN, 1000, 50, test, 0, false,
			"정말 맛있는 원두 단돈 천원", Bean.ARABICA, Acidity.CINNAMON, "부산 진구 유명가수가 좋아하는 원두",
			true, "20*50", "500ml", status, testTime, testTime, (short)3000, null
		);

		final ProductManagementDto expectedResponse = ProductManagementMapper.INSTANCE.toDto(entity);

		when(productManagementService.modifyToStatus(eq(productId), eq(status)))
			.thenReturn((expectedResponse));

		// when
		// then
		mockMvc.perform(put("/api/external/product/v1/status/{productId}/{status}", productId, status)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.result.id").value(productId))
			.andExpect(jsonPath("$.result.status").value(status.getCode()));
	}

	@Test
	void 상품_재고_변경() throws Exception {
		// Given
		final Integer productId = 1;
		final Integer changedStock = 10;

		final ProductManagementDto.Request.Stock dto = new ProductManagementDto.Request.Stock(productId, changedStock);

		final Product originalEntity = new Product(
			productId, ProductCategory.BEAN, 1000, 50, test, 0, false,
			"정말 맛있는 원두 단돈 천원", Bean.ARABICA, Acidity.CINNAMON, "부산 진구 유명가수가 좋아하는 원두",
			true, "20*50", "500ml", ProductStatus.AVAILABLE, testTime, testTime, (short)3000, null
		);
		final Product expectedEntity = new Product(
			productId, ProductCategory.BEAN, 1000, 50 + changedStock, test, 0, false,
			"정말 맛있는 원두 단돈 천원", Bean.ARABICA, Acidity.CINNAMON, "부산 진구 유명가수가 좋아하는 원두",
			true, "20*50", "500ml", ProductStatus.AVAILABLE, testTime, testTime, (short)3000, null

		);

		final ProductManagementDto expectedResponse = ProductManagementMapper.INSTANCE.toDto(expectedEntity);

		when(productManagementService.increaseToStock(dto))
			.thenReturn(expectedResponse);

		// when & then
		mockMvc.perform(put("/api/external/product/v1/stock/increase")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.result.id").value(productId))
			.andExpect(jsonPath("$.result.stock").value(originalEntity.getStock() + changedStock));
	}

	@Test
	void 상품_수정() throws Exception {
		final Integer productId = 1;
		final ProductManagementDto.Request.Modify dto = new ProductManagementDto.Request.Modify(
			true, 10000, Acidity.CINNAMON, Bean.ARABICA, ProductCategory.BEAN,
			"수정된", "커피", null, null, true);

		final Product expectedEntity = new Product(
			productId, dto.category(), dto.price(), 50, test, 0, dto.isDecaf(),
			dto.name(), dto.bean(), dto.acidity(), dto.information(),
			dto.isCrush(), "20 * 50", "500ml", ProductStatus.AVAILABLE, testTime, testTime, (short)3000, null
		);

		final MockMultipartFile mockThumbnailImage = createMockFile("thumbnailImage");

		List<MultipartFile> mockMultipartFiles = new ArrayList<>();

		final MockMultipartFile mockMultipartFile = createMockFile("images");

		final MockMultipartFile productJson = new MockMultipartFile("modifyProduct", "",
			"application/json", objectMapper.writeValueAsString(dto).getBytes(StandardCharsets.UTF_8));

		mockMultipartFiles.add(mockMultipartFile);

		final ProductManagementDto expectedResponse = ProductManagementMapper.INSTANCE.toDto(expectedEntity);

		when(productManagementService.modifyToProduct(eq(productId),
			eq(dto), eq(mockThumbnailImage), eq(mockMultipartFiles))).thenReturn(expectedResponse);

		// when & then
		ResultActions perform = mockMvc.perform(
			multipart("/api/external/product/v1/{productId}", productId)
				.file(productJson)
				.file(mockThumbnailImage)
				.file(mockMultipartFile)
				.with(new RequestPostProcessor() {
					@Override
					public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
						request.setMethod("PUT");
						return request;
					}
				})
				.contentType(MediaType.MULTIPART_FORM_DATA));
		perform
			.andExpect(status().isOk())
			.andDo(print())
			.andExpect(jsonPath("$.result.id").value(expectedResponse.getId()))
			.andExpect(jsonPath("$.result.category").value(expectedResponse.getCategory().name()))
			.andExpect(jsonPath("$.result.price").value(expectedResponse.getPrice()))
			.andExpect(jsonPath("$.result.name").value(expectedResponse.getName()))
			.andExpect(jsonPath("$.result.information").value(expectedResponse.getInformation()))
			.andExpect(jsonPath("$.result.isCrush").value(expectedResponse.getIsCrush()))
			.andExpect(jsonPath("$.result.isDecaf").value(expectedResponse.getIsDecaf()));
	}

	@Test
	void 상품_여러개_상태_변경() throws Exception {
		// Given
		final List<Integer> list = List.of(1, 2);
		final ProductStatus status = ProductStatus.DISCONTINUED;
		final ProductManagementDto.Request.BulkStatus request = new ProductManagementDto.Request.BulkStatus(list,
			status);

		final Product entity1 = new Product(
			1, ProductCategory.BEAN, 1000, 50, test, 0, false,
			"정말 맛있는 원두 단돈 천원", Bean.ARABICA, Acidity.CINNAMON, "부산 진구 유명가수가 좋아하는 원두",
			true, "20*50", "500ml", status, null, null, (short)3000, null
		);

		final Product entity2 = new Product(
			2, ProductCategory.BEAN, 1000, 50, test, 0, false,
			"정말 맛있는 원두 단돈 천원", Bean.ARABICA, Acidity.CINNAMON, "부산 진구 유명가수가 좋아하는 원두",
			true, "20*50", "500ml", status, null, null, (short)3000, null
		);

		List<Product> products = List.of(entity1, entity2);
		final List<ProductManagementDto> productManagementDtos = ProductManagementMapper.INSTANCE.productsToDtos(
			products);

		when(productManagementService.bulkModifyStatus(eq(request)))
			.thenReturn(productManagementDtos);

		// when, then
		mockMvc.perform(put("/api/external/product/v1/status")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.result[0].id").value(entity1.getId()))
			.andExpect(jsonPath("$.result[0].status").value(status.name()))
			.andExpect(jsonPath("$.result[1].id").value(entity2.getId()))
			.andExpect(jsonPath("$.result[1].status").value(status.name()));
	}

	private void verifyImages(List<Image> images, int index,
		List<ProductManagementDto.Request.Image> imageDtos) {
		if (index >= images.size()) {
			return;
		}

		Image image = images.get(index);
		ProductManagementDto.Request.Image imageDto = imageDtos.get(index);

		assertThat(image.getImageUrl()).isEqualTo(imageDto.imageUrl());
		assertThat(image.getIsThumbnail()).isEqualTo(imageDto.isThumbnail());
		assertThat(image.getSequenceNumber()).isEqualTo(imageDto.sequenceNumber());

		verifyImages(images, index + 1, imageDtos);
	}

	private void saveImages(List<ProductManagementDto.Request.Image> imageDtos, Product savedProduct) {
		List<Image> images = imageDtos.stream()
			.map(imageDto -> Image.ofCreate(imageDto.imageUrl(), imageDto.isThumbnail(), imageDto.sequenceNumber(),
				savedProduct))
			.collect(Collectors.toList());
		imageRepository.saveAll(images);
	}

	private static MockMultipartFile createMockFile(String name) {
		return new MockMultipartFile(name, "test.txt",
			"multipart/form-data",
			"test file".getBytes(StandardCharsets.UTF_8));
	}

}

