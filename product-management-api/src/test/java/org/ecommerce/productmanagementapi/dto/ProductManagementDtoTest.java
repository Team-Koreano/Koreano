package org.ecommerce.productmanagementapi.dto;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

import org.ecommerce.product.entity.Product;
import org.ecommerce.product.entity.SellerRep;
import org.ecommerce.product.entity.type.Acidity;
import org.ecommerce.product.entity.type.Bean;
import org.ecommerce.product.entity.type.ProductCategory;
import org.ecommerce.product.entity.type.ProductStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductManagementDtoTest {

	private static final LocalDateTime CREATE_DATETIME = LocalDateTime.now();
	private static final Product PRODUCT = new Product(
		1,
		ProductCategory.BEAN,
		19000,
		30,
		new SellerRep(1, "김이박"),
		0,
		true,
		"20년 전동 원두",
		Bean.ARABICA,
		Acidity.CITY,
		"엄청나게 맛있는 원두",
		ProductStatus.AVAILABLE,
		CREATE_DATETIME,
		CREATE_DATETIME
	);

	@Test
	void 상품_응답() {
		final ProductManagementDto.Response resp = ProductManagementDto.Response.of(PRODUCT);

		assertThat(resp.id()).isEqualTo(1);
		assertThat(resp.category()).isEqualTo(ProductCategory.BEAN.getTitle());
		assertThat(resp.price()).isEqualTo(19000);
		assertThat(resp.stock()).isEqualTo(30);
		assertThat(resp.bizName()).isEqualTo("김이박");
		assertThat(resp.favoriteCount()).isZero();
		assertThat(resp.isDecaf()).isTrue();
		assertThat(resp.name()).isEqualTo("20년 전동 원두");
		assertThat(resp.bean()).isEqualTo(Bean.ARABICA.getTitle());
		assertThat(resp.acidity()).isEqualTo(Acidity.CITY.getTitle());
		assertThat(resp.information()).isEqualTo("엄청나게 맛있는 원두");
		assertThat(resp.status()).isEqualTo(ProductStatus.AVAILABLE.getTitle());
		assertThat(resp.createDatetime()).isEqualTo(CREATE_DATETIME);
	}

}
