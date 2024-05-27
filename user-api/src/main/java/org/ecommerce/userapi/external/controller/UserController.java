package org.ecommerce.userapi.external.controller;

import org.ecommerce.common.vo.Response;
import org.ecommerce.userapi.dto.AccountDto;
import org.ecommerce.userapi.dto.AccountMapper;
import org.ecommerce.userapi.dto.AddressDto;
import org.ecommerce.userapi.dto.AddressMapper;
import org.ecommerce.userapi.dto.UserDto;
import org.ecommerce.userapi.dto.UserMapper;
import org.ecommerce.userapi.dto.request.CreateAccountRequest;
import org.ecommerce.userapi.dto.request.CreateAddressRequest;
import org.ecommerce.userapi.dto.request.CreateUserRequest;
import org.ecommerce.userapi.dto.request.LoginUserRequest;
import org.ecommerce.userapi.dto.request.WithdrawalUserRequest;
import org.ecommerce.userapi.dto.response.CreateAccountResponse;
import org.ecommerce.userapi.dto.response.CreateAddressResponse;
import org.ecommerce.userapi.dto.response.CreateUserResponse;
import org.ecommerce.userapi.dto.response.LoginUserResponse;
import org.ecommerce.userapi.external.service.UserService;
import org.ecommerce.userapi.security.AuthDetails;
import org.ecommerce.userapi.security.custom.CurrentUser;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/external/users/v1")
public class UserController {

	private final UserService userService;

	@PostMapping()
	public Response<CreateUserResponse> register(@RequestBody final CreateUserRequest createRequest) {
		final UserDto userDto = userService.registerRequest(createRequest);
		return new Response<>(HttpStatus.OK.value(), UserMapper.INSTANCE.toResponse(userDto));
	}

	@PostMapping("/login")
	public Response<LoginUserResponse> login(
		@RequestBody final LoginUserRequest login,
		HttpServletResponse response
	) {
		UserDto userDto = userService.loginRequest(login, response);
		return new Response<>(HttpStatus.OK.value(), LoginUserResponse.of(userDto));
	}

	@PostMapping("/logout")
	public Response<String> logout(@CurrentUser final AuthDetails authDetails) {
		userService.logoutRequest(authDetails);
		return new Response<>(HttpStatus.OK.value(), "로그아웃에 성공하였습니다");
	}

	@PostMapping("/address")
	public Response<CreateAddressResponse> address(
		@CurrentUser final AuthDetails authDetails,
		@RequestBody @Valid final CreateAddressRequest address) {
		final AddressDto addressDto = userService.createAddress(authDetails, address);
		return new Response<>(HttpStatus.OK.value(), AddressMapper.INSTANCE.toResponse(addressDto));
	}

	@PostMapping("/account")
	public Response<CreateAccountResponse> account(
		@CurrentUser final AuthDetails authDetails,
		@RequestBody @Valid final CreateAccountRequest account) {
		final AccountDto accountDto = userService.createAccount(authDetails, account);
		return new Response<>(HttpStatus.OK.value(), AccountMapper.INSTANCE.toResponse(accountDto));
	}

	@PostMapping("/reissue")
	public Response<LoginUserResponse> reissueAccessToken(
		@RequestHeader(HttpHeaders.AUTHORIZATION) final String bearerToken,
		HttpServletResponse response
	) {
		UserDto userDto = userService.reissueAccessToken(bearerToken, response);
		return new Response<>(HttpStatus.OK.value(), LoginUserResponse.of(userDto));
	}

	@DeleteMapping()
	public Response<String> withdrawUser(
		@CurrentUser final AuthDetails authDetails,
		@Valid @RequestBody final WithdrawalUserRequest withdrawal
	) {
		userService.withdrawUser(withdrawal, authDetails);
		return new Response<>(HttpStatus.OK.value(), "탈퇴에 성공하였습니다");
	}
}