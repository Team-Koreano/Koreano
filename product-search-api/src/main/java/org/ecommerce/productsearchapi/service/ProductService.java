package org.ecommerce.productsearchapi.service;

import java.util.List;

import org.ecommerce.productsearchapi.dto.ProductDto;
import org.ecommerce.productsearchapi.entity.Product;
import org.ecommerce.productsearchapi.entity.SellerRep;
import org.ecommerce.productsearchapi.entity.type.Acidity;
import org.ecommerce.productsearchapi.entity.type.Bean;
import org.ecommerce.productsearchapi.entity.type.Category;
import org.ecommerce.productsearchapi.entity.type.Status;
import org.ecommerce.productsearchapi.repository.ProductRepository;
import org.ecommerce.productsearchapi.repository.SellerRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

	private final ProductRepository productRepository;
	private final SellerRepository sellerRepository;

	public void registerSeller() {
		SellerRep sellerRep = SellerRep.builder()
			.bizName("노원호")
			.build();
		sellerRepository.save(sellerRep);
	}

	public void createProduct(ProductDto.Request.CreateProductDto productDto) {

		SellerRep sellerRep = sellerRepository.findById(productDto.sellerId()).get();

		Product product = Product.builder()
			.category(Category.valueOf(productDto.category()))
			.price(productDto.price())
			.stock(productDto.stock())
			.sellerRep(sellerRep)
			.isDecaf(productDto.isDecaf())
			.name(productDto.name())
			.bean(Bean.valueOf(productDto.bean()))
			.status(Status.AVAILABLE)
			.acidity(Acidity.valueOf(productDto.acidity()))
			.information(productDto.information())
			.favoriteCount(0)
			.build();

		productRepository.save(product);

	}

	public List<ProductDto.Response.GetProductDto> getProductDtoList() {
		List<Product> productList = productRepository.findAll();
		return productList.stream().map(product ->
				ProductDto.Response.GetProductDto.builder()
					.id(product.getId())
					.name(product.getName())
					.information(product.getInformation())
					.status(product.getStatus().getTitle())
					.is_decaf(product.getIsDecaf())
					.price(product.getPrice())
					.stock(product.getStock())
					.acidity(product.getAcidity().getTitle())
					.category(product.getCategory().getTitle())
					.sellerId(product.getSellerRep().getId())
					.bean(product.getBean().getTitle())
					.build()
			).toList();
	}

}
