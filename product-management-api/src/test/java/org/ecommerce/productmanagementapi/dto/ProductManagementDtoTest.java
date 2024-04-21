package org.ecommerce.productmanagementapi.dto;

import java.util.List;

import org.ecommerce.product.entity.type.Acidity;
import org.ecommerce.product.entity.type.Bean;
import org.ecommerce.product.entity.type.ProductCategory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)

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
	}
}
