package org.ecommerce.userapi.controller;

import org.ecommerce.common.vo.Response;
import org.ecommerce.userapi.dto.UserDto;
import org.ecommerce.userapi.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

	private final UserService userService;

	@PostMapping("/register")
	public Response<UserDto.Response.Register> register(@RequestBody UserDto.Request.Register register) {
		UserDto.Response.Register responseUser = userService.registerUser(register);
		return new Response<>(HttpStatus.OK.value(), responseUser);
	}
}