package org.ecommerce.productmanagementapi.dto;

import java.util.List;

import org.ecommerce.product.entity.enumerated.Acidity;
import org.ecommerce.product.entity.enumerated.Bean;
import org.ecommerce.product.entity.enumerated.ProductCategory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductManagementDtoTest {

	@Test
	void 상품_등록() {

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
				"20 * 50"
			);
	}
}
