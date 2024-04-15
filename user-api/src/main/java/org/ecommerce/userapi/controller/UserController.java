package org.ecommerce.userapi.controller;

import org.ecommerce.common.vo.Response;
import org.ecommerce.userapi.dto.UserDto;
import org.ecommerce.userapi.security.AuthDetails;
import org.ecommerce.userapi.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/users/v1")
public class UserController {

	private final UserService userService;


	@PostMapping()
	public Response<UserDto.Response.Register> register(@RequestBody UserDto.Request.Register register) {
		UserDto.Response.Register responseUser = userService.registerRequest(register);
		return new Response<>(HttpStatus.OK.value(), responseUser);
	}
	@PostMapping("/login")
	public Response<UserDto.Response.Login> login(@RequestBody UserDto.Request.Login login) {
		UserDto.Response.Login responseLogin = userService.loginRequest(login);
		return new Response<>(HttpStatus.OK.value(), responseLogin);
	}

	@PostMapping("/logout")
	public Response<?> logout(@AuthenticationPrincipal AuthDetails authDetails){
		userService.logoutRequest(authDetails);
		return new Response<>(HttpStatus.OK.value(), "로그아웃에 성공하였습니다");
	}
}