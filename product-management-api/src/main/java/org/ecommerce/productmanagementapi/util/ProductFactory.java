package org.ecommerce.productmanagementapi.util;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.product.entity.Product;
import org.ecommerce.product.entity.SellerRep;
import org.ecommerce.product.entity.enumerated.ProductCategory;
import org.ecommerce.productmanagementapi.dto.ProductManagementDto;
import org.ecommerce.productmanagementapi.exception.ProductManagementErrorCode;

public interface ProductFactory {
	Product createProduct(ProductManagementDto.Request.Register request, SellerRep seller);

	void modifyProduct(Product product, ProductManagementDto.Request.Modify modifyProduct);

	static ProductFactory getFactory(ProductCategory category) {
		return switch (category) {
			case BEAN -> new BeanProductFactory();
			case CUP, BLENDER, MACHINE, CUP_STAND -> new DefaultProductFactory();
			default -> throw new CustomException(ProductManagementErrorCode.NOT_FOUND_CATEGORY);
		};
	}

	class BeanProductFactory implements ProductFactory {
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

		@Override
		public void modifyProduct(Product product, ProductManagementDto.Request.Modify modifyProduct) {
			product.toModify(
				modifyProduct.category(),
				modifyProduct.price(),
				modifyProduct.name(),
				modifyProduct.bean(),
				modifyProduct.acidity(),
				modifyProduct.information(),
				modifyProduct.isCrush(),
				modifyProduct.isDecaf(),
				null
			);
		}
	}

	class DefaultProductFactory implements ProductFactory {
		@Override
		public Product createProduct(ProductManagementDto.Request.Register request, SellerRep seller) {
			return Product.createDefault(request.category(), request.price(), request.stock(), request.name(),
				request.information(), request.size(), seller);
		}

		@Override
		public void modifyProduct(Product product, ProductManagementDto.Request.Modify modifyProduct) {
			product.toModify(
				modifyProduct.category(),
				modifyProduct.price(),
				modifyProduct.name(),
				null, // Bean 필드
				null, // Bean 필드
				modifyProduct.information(),
				null, // Bean 필드
				null,  // Bean 필드
				modifyProduct.size()
			);
		}
	}
}