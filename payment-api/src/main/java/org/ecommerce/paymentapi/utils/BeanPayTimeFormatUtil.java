package org.ecommerce.paymentapi.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BeanPayTimeFormatUtil {
	static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

	public static LocalDateTime stringToDateTime(String dateTime) {
		return LocalDateTime.parse(dateTime, formatter);
	}
}
