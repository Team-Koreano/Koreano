package org.ecommerce.productmanagementapi.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;

import org.ecommerce.product.entity.Product;
import org.ecommerce.product.entity.SellerRep;
import org.ecommerce.product.entity.type.Acidity;
import org.ecommerce.product.entity.type.Bean;
import org.ecommerce.product.entity.type.ProductCategory;
import org.ecommerce.productmanagementapi.repository.ImageRepository;
import org.ecommerce.productmanagementapi.repository.ProductRepository;
import org.ecommerce.productmanagementapi.dto.ProductManagementDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class ProductManagementServiceTest {

	@InjectMocks
	private ProductManagementService productManagementService;

	@Mock
	private ProductRepository productRepository;

	@Mock
	private ImageRepository imageRepository;
	private static final SellerRep test = new SellerRep(1,"TEST");

	@Test
	void 상품_등록(){
		final List<ProductManagementDto.Request.Register.ImageDto> imageDtos = List.of(
			new ProductManagementDto.Request.Register.ImageDto("image1.jpg", true, (short) 1),
			new ProductManagementDto.Request.Register.ImageDto("image2.jpg", false, (short) 2),
			new ProductManagementDto.Request.Register.ImageDto("image3.jpg", false, (short) 3)
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

		given(productRepository.save(any(Product.class))).willReturn(
			product
		);
		final ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);

		final ProductManagementDto productManagementDto = productManagementService.productRegister(productDtos);

		verify(productRepository,times(1)).save(captor.capture());
		verify(imageRepository, times(1)).saveAll(anyList());

		assertThat(productManagementDto.getAcidity()).isEqualTo(captor.getValue().getAcidity());
		assertThat(productManagementDto.getBean()).isEqualTo(captor.getValue().getBean());
		assertThat(productManagementDto.getCategory()).isEqualTo(captor.getValue().getCategory());
		assertThat(productManagementDto.getInformation()).isEqualTo(captor.getValue().getInformation());
		assertThat(productManagementDto.getName()).isEqualTo(captor.getValue().getName());
		assertThat(productManagementDto.getPrice()).isEqualTo(captor.getValue().getPrice());
		assertThat(productManagementDto.getStock()).isEqualTo(captor.getValue().getStock());
		assertThat(productManagementDto.getSellerRep()).usingRecursiveComparison().isEqualTo(captor.getValue().getSellerRep());
		assertThat(productManagementDto.getIsCrush()).isEqualTo(captor.getValue().getIsCrush());
		assertThat(productManagementDto.getIsDecaf()).isEqualTo(captor.getValue().getIsDecaf());
	}
}
