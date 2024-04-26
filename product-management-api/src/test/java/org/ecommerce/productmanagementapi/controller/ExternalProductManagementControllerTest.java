package org.ecommerce.productmanagementapi.controller;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.ecommerce.product.entity.Image;
import org.ecommerce.product.entity.Product;
import org.ecommerce.product.entity.SellerRep;
import org.ecommerce.product.entity.type.Acidity;
import org.ecommerce.product.entity.type.Bean;
import org.ecommerce.product.entity.type.ProductCategory;
import org.ecommerce.product.entity.type.ProductStatus;
import org.ecommerce.productmanagementapi.dto.ProductManagementDto;
import org.ecommerce.productmanagementapi.dto.ProductManagementMapper;
import org.ecommerce.productmanagementapi.repository.ImageRepository;
import org.ecommerce.productmanagementapi.service.ProductManagementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
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
		final List<ProductManagementDto.Request.Register.ImageDto> imageDtos = List.of(
			new ProductManagementDto.Request.Register.ImageDto("image1.jpg", true, (short)1),
			new ProductManagementDto.Request.Register.ImageDto("image2.jpg", false, (short)2),
			new ProductManagementDto.Request.Register.ImageDto("image3.jpg", false, (short)3)
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
				imageDtos
			);

		final Product product = Product.ofCreate(
			productDtos.category(),
			productDtos.price(),
			productDtos.stock(),
			productDtos.name(),
			productDtos.bean(),
			productDtos.acidity(),
			productDtos.information(),
			productDtos.isCrush(),
			productDtos.isDecaf(),
			test
		);

		final ProductManagementDto productConvertToDto = ProductManagementMapper.INSTANCE.toDto(product);

		final ProductManagementDto.Response expectedResponse = ProductManagementMapper.INSTANCE.toResponse(
			productConvertToDto);
		saveImages(imageDtos, product);
		when(productManagementService.productRegister(productDtos)).thenReturn(productConvertToDto);
		//when
		//then
		final ArgumentCaptor<List<Image>> imageListCaptor = ArgumentCaptor.forClass(List.class);
		verify(imageRepository, times(1)).saveAll(imageListCaptor.capture());
		final List<Image> savedImages = imageListCaptor.getValue();

		verifyImages(savedImages, 0, imageDtos);

		ResultActions resultActions = mockMvc.perform(post("/api/external/productmanagement/v1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(productDtos))
			)
			.andExpect(status().isOk());
		resultActions
			.andExpect(jsonPath("$.result.name").value(expectedResponse.name()))
			.andExpect(jsonPath("$.result.price").value(expectedResponse.price()))
			.andExpect(jsonPath("$.result.stock").value(expectedResponse.stock()))
			.andExpect(jsonPath("$.result.acidity").value(expectedResponse.acidity()))
			.andExpect(jsonPath("$.result.bean").value(expectedResponse.bean()))
			.andExpect(jsonPath("$.result.category").value(expectedResponse.category()))
			.andExpect(jsonPath("$.result.information").value(expectedResponse.information()))
			.andExpect(jsonPath("$.result.status").value(expectedResponse.status()))
			.andExpect(jsonPath("$.result.isCrush").value(expectedResponse.isCrush()))
			.andExpect(jsonPath("$.result.bizName").value(expectedResponse.bizName()))
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
			true, status, testTime, testTime, null
		);

		final ProductManagementDto expectedResponse = ProductManagementMapper.INSTANCE.toDto(entity);

		when(productManagementService.modifyToStatus(eq(productId), eq(status)))
			.thenReturn((expectedResponse));

		// when
		// then
		mockMvc.perform(put("/api/external/productmanagement/v1/status/{productId}/{status}", productId, status)
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
			true, ProductStatus.AVAILABLE, testTime, testTime, null
		);

		final Product expectedEntity = new Product(
			productId, ProductCategory.BEAN, 1000, 50 + changedStock, test, 0, false,
			"정말 맛있는 원두 단돈 천원", Bean.ARABICA, Acidity.CINNAMON, "부산 진구 유명가수가 좋아하는 원두",
			true, ProductStatus.AVAILABLE, testTime, testTime, null
		);

		final ProductManagementDto expectedResponse = ProductManagementMapper.INSTANCE.toDto(expectedEntity);

		when(productManagementService.modifyToStock(dto))
			.thenReturn(expectedResponse);

		// when & then
		mockMvc.perform(put("/api/external/productmanagement/v1/stock")
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
			"수정된", "커피", true);

		final Product expectedEntity = new Product(
			productId, dto.category(), dto.price(), null, null, null, dto.isDecaf(),
			dto.name(), dto.bean(), dto.acidity(), dto.information(),
			dto.isCrush(), null, null, null, null
		);

		final ProductManagementDto expectedResponse = ProductManagementMapper.INSTANCE.toDto(expectedEntity);

		when(productManagementService.modifyToProduct(eq(productId),
			eq(dto))).thenReturn(expectedResponse);

		// when & then
		mockMvc.perform(put("/api/external/productmanagement/v1/{productId}", productId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.result.id").value(expectedResponse.getId()))
			.andExpect(jsonPath("$.result.category").value(expectedResponse.getCategory().name()))
			.andExpect(jsonPath("$.result.price").value(expectedResponse.getPrice()))
			.andExpect(jsonPath("$.result.name").value(expectedResponse.getName()))
			.andExpect(jsonPath("$.result.information").value(expectedResponse.getInformation()))
			.andExpect(jsonPath("$.result.isCrush").value(expectedResponse.getIsCrush()))
			.andExpect(jsonPath("$.result.isDecaf").value(expectedResponse.getIsDecaf()));
	}

	private void verifyImages(List<Image> images, int index,
		List<ProductManagementDto.Request.Register.ImageDto> imageDtos) {
		if (index >= images.size()) {
			return;
		}

		Image image = images.get(index);
		ProductManagementDto.Request.Register.ImageDto imageDto = imageDtos.get(index);

		assertThat(image.getImageUrl()).isEqualTo(imageDto.imageUrl());
		assertThat(image.getIsThumbnail()).isEqualTo(imageDto.isThumbnail());
		assertThat(image.getSequenceNumber()).isEqualTo(imageDto.sequenceNumber());

		verifyImages(images, index + 1, imageDtos);
	}

	private void saveImages(List<ProductManagementDto.Request.Register.ImageDto> imageDtos, Product savedProduct) {
		List<Image> images = imageDtos.stream()
			.map(imageDto -> Image.ofCreate(imageDto.imageUrl(), imageDto.isThumbnail(), imageDto.sequenceNumber(),
				savedProduct))
			.collect(Collectors.toList());
		imageRepository.saveAll(images);
	}
}
