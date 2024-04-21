package org.ecommerce.productmanagementapi.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.ecommerce.product.entity.Product;
import org.ecommerce.product.entity.SellerRep;
import org.ecommerce.product.entity.type.Acidity;
import org.ecommerce.product.entity.type.Bean;
import org.ecommerce.product.entity.type.ProductCategory;
import org.ecommerce.productmanagementapi.dto.ProductManagementDto;
import org.ecommerce.productmanagementapi.dto.ProductManagementMapper;
import org.ecommerce.productmanagementapi.service.ProductManagementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
class ProductManagementControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private WebApplicationContext context;

	@MockBean
	private ProductManagementService productManagementService;

	private static final SellerRep test = new SellerRep(2, "TEST");

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
			new ProductManagementDto.Request.Register.ImageDto("image1.jpg", true, (short) 1),
			new ProductManagementDto.Request.Register.ImageDto("image2.jpg", false, (short) 2),
			new ProductManagementDto.Request.Register.ImageDto("image3.jpg", false, (short) 3)
		);

		final ProductManagementDto.Request.Register productDtos =
			new ProductManagementDto.Request.Register(
				true,
				1000,
				"testBiz",
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

		final ProductManagementDto.Response expectedResponse = ProductManagementDto.Response.of(productConvertToDto);


		when(productManagementService.productRegister(productDtos)).thenReturn(productConvertToDto);
		//when
		//then
		mockMvc.perform(post("/api/productmanagement/v1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(productDtos))
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.result.name").value(expectedResponse.name()))
			.andExpect(jsonPath("$.result.price").value(expectedResponse.price()))
			.andExpect(jsonPath("$.result.bizName").value(expectedResponse.bizName()))
			.andExpect(jsonPath("$.result.stock").value(expectedResponse.stock()))
			.andExpect(jsonPath("$.result.acidity").value(expectedResponse.acidity()))
			.andExpect(jsonPath("$.result.bean").value(expectedResponse.bean()))
			.andExpect(jsonPath("$.result.category").value(expectedResponse.category()))
			.andExpect(jsonPath("$.result.information").value(expectedResponse.information()))
			.andExpect(jsonPath("$.result.status").value(expectedResponse.status()))
			.andExpect(jsonPath("$.result.isCrush").value(expectedResponse.isCrush()))
			.andDo(print());
	}
}
