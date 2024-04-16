package org.ecommerce.userapi.controller;

import java.util.Set;

import org.ecommerce.common.vo.Response;
import org.ecommerce.userapi.dto.UserDto;
import org.ecommerce.userapi.security.AuthDetails;
import org.ecommerce.userapi.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
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

	// TODO : 계좌, 주소 테이블 생성 로직 구현
	//  AccessToken 만료시 RefreshToken 을 통해 Reissue 로직 구현

	private final UserService userService;
	@PostMapping()
	public Response<UserDto.Response.Register> register(@RequestBody final UserDto.Request.Register register) {
 		final UserDto userDto = userService.registerRequest(register);
		return new Response<>(HttpStatus.OK.value(), UserDto.Response.Register.of(userDto));
	}
	@PostMapping("/login")
	public Response<UserDto.Response.Login> login(@RequestBody final UserDto.Request.Login login) {
		UserDto userDto = userService.loginRequest(login);
		return new Response<>(HttpStatus.OK.value(), UserDto.Response.Login.of(userDto));
	}

	@PostMapping("/logout")
	public Response<String> logout(@AuthenticationPrincipal final AuthDetails authDetails){
		userService.logoutRequest(authDetails);
		return new Response<>(HttpStatus.OK.value(), "로그아웃에 성공하였습니다");
	}
	@GetMapping("/test")
	public Response<?> test(@AuthenticationPrincipal final AuthDetails authDetails){
		Set<SimpleGrantedAuthority> authoritySet = authDetails.getAuthoritySet();
		System.out.println("authoritySet = " + authoritySet);
		return new Response<>(200,"성공");
	}
}