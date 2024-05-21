package org.ecommerce.orderapi.stock.entity;

import static org.ecommerce.orderapi.stock.entity.enumerated.StockOperationResult.*;
import static org.ecommerce.orderapi.stock.entity.enumerated.StockOperationType.*;
import static org.ecommerce.orderapi.stock.exception.StockErrorCode.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.orderapi.stock.entity.enumerated.StockOperationResult;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "stock", indexes = @Index(name = "idx_productId", columnList = "productId"))
@Entity
@Getter
public class Stock {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column
	private Integer productId;

	@Column
	private Integer total;

	@CreationTimestamp
	@Column
	private LocalDateTime createDatetime;

	@OneToMany(mappedBy = "stock", cascade = CascadeType.ALL)
	private List<StockHistory> stockHistories = new ArrayList<>();

	public static Stock of(
			final Integer productId,
			final Integer total
	) {
		final Stock stock = new Stock();
		stock.productId = productId;
		stock.total = total;
		stock.stockHistories.add(
				StockHistory.ofRecord(stock, null, INCREASE, SUCCESS));
		return stock;
	}

	public StockOperationResult decreaseTotal(
			final Long orderItemId,
			final Integer quantity
	) {

		if (!hasStock(quantity)) {
			stockHistories.add(
					StockHistory.ofRecord(this, orderItemId, DECREASE, TOTAL_LIMIT)
			);
			return TOTAL_LIMIT;
		}

		total -= quantity;
		stockHistories.add(
				StockHistory.ofRecord(this, orderItemId, DECREASE, SUCCESS)
		);
		return SUCCESS;
	}

	public void increaseTotal(final Long orderItemId, final Integer quantity) {
		validateStockHistory(orderItemId);

		total += quantity;
		stockHistories.add(
				StockHistory.ofRecord(
						this,
						orderItemId,
						INCREASE,
						SUCCESS
				)
		);
	}

	public boolean hasStock(Integer quantity) {
		return this.total >= quantity;
	}

	private void validateStockHistory(final Long orderItemId) {
		StockHistory stockHistory = getStockHistoryByOrderItemId(orderItemId);
		if (!stockHistory.isOperationTypeDecrease()) {
			throw new CustomException(
					MUST_DECREASE_STOCK_OPERATION_TYPE_TO_INCREASE_STOCK);
		}

		if (!stockHistory.isOperationResultSuccess()) {
			throw new CustomException(MUST_SUCCESS_OPERATION_RESULT_TO_INCREASE_STOCK);
		}
	}

	private StockHistory getStockHistoryByOrderItemId(final Long orderItemId) {
		return stockHistories.stream()
				.filter(stockHistory -> stockHistory.getOrderItemId().equals(orderItemId))
				.findFirst()
				.orElseThrow(() -> new CustomException(NOT_FOUND_STOCK_HISTORY));
	}
}
