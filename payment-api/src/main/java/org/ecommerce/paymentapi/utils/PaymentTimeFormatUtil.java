package org.ecommerce.paymentapi.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PaymentTimeFormatUtil {
	private static final DateTimeFormatter formatter =
		DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

	public static LocalDateTime stringToDateTime(String dateTime) {
		return LocalDateTime.parse(dateTime, formatter);
	}
}
