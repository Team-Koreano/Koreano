package org.ecommerce.productmanagementapi.dto;

import java.util.List;

import org.ecommerce.product.entity.enumerated.Acidity;
import org.ecommerce.product.entity.enumerated.Bean;
import org.ecommerce.product.entity.enumerated.ProductCategory;
import org.ecommerce.productmanagementapi.dto.request.CreateProductRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductManagementDtoTest {

	@Test
	void 상품_등록() {

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
				"20 * 50",
				"500ml",
				(short)3000
			);
	}
}
