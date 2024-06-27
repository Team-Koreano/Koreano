package org.ecommerce.productapi;

import org.ecommerce.common.provider.JwtProvider;
import org.ecommerce.common.security.CustomAuthProvider;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;

public abstract class ControllerTest {

	@MockBean
	private JwtProvider jwtProvider;

	@MockBean
	private CustomAuthProvider customAuthProvider;

	@MockBean
	private AuthenticationManager authenticationManager;

	@MockBean
	private ProviderManager providerManager;

}
