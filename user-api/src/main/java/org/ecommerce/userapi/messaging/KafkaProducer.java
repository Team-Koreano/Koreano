package org.ecommerce.userapi.messaging;

import org.ecommerce.userapi.dto.SellerCreateMessage;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class KafkaProducer {
	private final KafkaTemplate<String, Object> kafkaTemplate;

	public void send(final SellerCreateMessage message) {
		kafkaTemplate.send("create-seller-topic", message);
	}
}
