package org.ecommerce.orderapi.stock.messaging;

import org.ecommerce.orderapi.stock.dto.StockOperationMessage;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class KafkaProducer {
	private final KafkaTemplate<String, Object> kafkaTemplate;

	public void send(final StockOperationMessage message) {
		kafkaTemplate.send("topic", message);
	}
}
