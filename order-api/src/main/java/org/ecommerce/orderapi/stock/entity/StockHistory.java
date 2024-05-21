package org.ecommerce.orderapi.stock.entity;

import static org.ecommerce.orderapi.stock.entity.enumerated.StockOperationResult.*;
import static org.ecommerce.orderapi.stock.entity.enumerated.StockOperationType.*;

import java.time.LocalDateTime;

import org.ecommerce.orderapi.order.entity.OrderItem;
import org.ecommerce.orderapi.stock.entity.enumerated.StockOperationResult;
import org.ecommerce.orderapi.stock.entity.enumerated.StockOperationType;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "stock_history")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class StockHistory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	private Stock stock;

	@Column(nullable = false)
	private Long orderItemId;

	@Column
	@Enumerated(EnumType.STRING)
	private StockOperationType operationType;

	@Column
	@Enumerated(EnumType.STRING)
	private StockOperationResult operationResult;

	@CreationTimestamp
	@Column
	private LocalDateTime operationDatetime;

	static StockHistory ofRecord(
			final Stock stock,
			final Long orderItemId,
			final StockOperationType operationType,
			final StockOperationResult operationResult
	) {
		final StockHistory stockHistory = new StockHistory();
		stockHistory.stock = stock;
		stockHistory.orderItemId = orderItemId;
		stockHistory.operationType = operationType;
		stockHistory.operationResult = operationResult;
		return stockHistory;
	}

	boolean isOperationTypeDecrease() {
		return this.operationType == DECREASE;
	}

	boolean isOperationResultSuccess() {
		return this.operationResult == SUCCESS;
	}
}
