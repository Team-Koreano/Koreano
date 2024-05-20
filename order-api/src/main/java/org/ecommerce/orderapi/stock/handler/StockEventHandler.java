package org.ecommerce.orderapi.stock.handler;

import java.time.LocalDateTime;
import java.util.List;

import org.ecommerce.orderapi.stock.dto.StockDto;
import org.ecommerce.orderapi.stock.event.StockDecreasedEvent;
import org.ecommerce.orderapi.stock.event.StockIncreasedEvent;
import org.ecommerce.orderapi.stock.service.StockHelper;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StockEventHandler {

	private final StockHelper stockHelper;

	public List<StockDto> decreaseStocks(final Long orderId) {
		List<StockDto> stockDtos = stockHelper.decreaseStocks(orderId);

		// TODO stockDecreasedEvents 발행
		List<StockDecreasedEvent> stockDecreasedEvents = stockDtos.stream()
				.map(stockDto -> new StockDecreasedEvent(
						stockDto.getId(),
						LocalDateTime.now())
				).toList();
		return stockDtos;
	}

	public StockDto increaseStock(final Long orderId, final Long orderItemId) {
		StockDto stockDto = stockHelper.increaseStock(orderId, orderItemId);
		// TODO stockIncreasedEvent 발행
		new StockIncreasedEvent(stockDto.getId(), LocalDateTime.now());
		return stockDto;
	}
}
