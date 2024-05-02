package org.ecommerce.orderapi.service;

import static org.ecommerce.orderapi.entity.enumerated.ProductStatus.*;
import static org.ecommerce.orderapi.exception.OrderErrorCode.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.orderapi.dto.ProductDto;
import org.ecommerce.orderapi.entity.Product;
import org.ecommerce.orderapi.entity.Stock;
import org.ecommerce.orderapi.util.ProductOperation;
import org.ecommerce.orderapi.util.StockOperation;
import org.redisson.api.RTransaction;
import org.redisson.api.RedissonClient;
import org.redisson.api.TransactionOptions;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

	private final RedissonClient redissonClient;

	/**
	 * MockData 만드는 메소드입니다.
	 * @author ${Juwon}
	 */
	public void saveMock() {

		List<Product> mockProducts = Arrays.asList(
				new Product(101, "아라비카", 10000, "seller1", AVAILABLE),
				new Product(102, "로부스타", 20000, "seller2", AVAILABLE),
				new Product(103, "리베리카", 30000, "seller3", AVAILABLE)
		);

		List<Stock> mockStocks = Arrays.asList(
				new Stock(101, 10),
				new Stock(102, 20),
				new Stock(103, 15)
		);
		RTransaction transaction = redissonClient.createTransaction(
				TransactionOptions.defaults());
		IntStream.range(0, mockStocks.size())
				.forEach(i -> {
					StockOperation.setStock(transaction, mockStocks.get(i));
					ProductOperation.setProduct(transaction, mockProducts.get(i));
				});
		transaction.commit();
	}

	/**
	 * MockData 가져오는 메소드입니다.
	 * @author ${Juwon}
	 */
	public ProductDto getMockData(Integer productId) {
		Stock stock = StockOperation.getStock(redissonClient, productId)
				.orElseThrow(() -> new CustomException(INSUFFICIENT_STOCK_INFORMATION));
		Product product = ProductOperation.getProduct(redissonClient, productId)
				.orElseThrow(() -> new CustomException(NOT_FOUND_PRODUCT_ID));
		return new ProductDto(
				product.getId(),
				product.getPrice(),
				stock.getTotal(),
				product.getSeller());
	}
}
