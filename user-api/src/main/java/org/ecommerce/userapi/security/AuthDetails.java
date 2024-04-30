package org.ecommerce.userapi.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthDetails {
	private Integer id;
	private String email;
	private String roll;
}
