package org.ecommerce.orderapi.entity;

import static org.ecommerce.orderapi.entity.enumerated.StockOperationType.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
				StockHistory.ofRecord(stock, null, INCREASE));
		return stock;
	}

	public void decreaseTotalStock(
			final Integer quantity,
			final OrderDetail orderDetail
	) {
		this.total -= quantity;
		this.stockHistories = new ArrayList<>(this.stockHistories);
		this.stockHistories.add(
				StockHistory.ofRecord(
						this,
						orderDetail,
						DECREASE
				));
	}

	public void increaseTotalStock(
			final OrderDetail orderDetail
	) {
		this.total += orderDetail.getQuantity();
		this.stockHistories = new ArrayList<>(this.stockHistories);
		this.stockHistories.add(
				StockHistory.ofRecord(
						this,
						orderDetail,
						INCREASE
				));
	}

	public boolean hasStock(Integer quantity) {
		return this.total >= quantity;
	}
}
