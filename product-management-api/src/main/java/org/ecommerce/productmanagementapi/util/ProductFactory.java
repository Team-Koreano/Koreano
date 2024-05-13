package org.ecommerce.productmanagementapi.util;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.product.entity.Product;
import org.ecommerce.product.entity.SellerRep;
import org.ecommerce.product.entity.enumerated.ProductCategory;
import org.ecommerce.productmanagementapi.dto.ProductManagementDto;
import org.ecommerce.productmanagementapi.exception.ProductManagementErrorCode;

public abstract class ProductFactory {
	public abstract Product createProduct(ProductManagementDto.Request.Register request, SellerRep seller);

	public static ProductFactory getFactory(ProductCategory category) {
		return switch (category) {
			case BEAN -> new BeanProductFactory();
			case CUP, BLENDER, MACHINE, CUP_STAND -> new DefaultProductFactory();
			default -> throw new CustomException(ProductManagementErrorCode.NOT_FOUND_CATEGORY);
		};
	}

	static class BeanProductFactory extends ProductFactory {
		@Override
		public Product createProduct(ProductManagementDto.Request.Register request, SellerRep seller) {
			return Product.createBean(
				request.category(),
				request.price(),
				request.stock(),
				request.name(),
				request.bean(),
				request.acidity(),
				request.information(),
				request.isCrush(),
				request.isDecaf(),
				seller
			);
		}
	}

	static class DefaultProductFactory extends ProductFactory {
		@Override
		public Product createProduct(ProductManagementDto.Request.Register request, SellerRep seller) {
			return Product.createDefault(request.category(), request.price(), request.stock(), request.name(),
				request.information(), request.size(), seller);
		}
	}
}