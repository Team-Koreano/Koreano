package org.ecommerce.userapi.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ecommerce.userapi.dto.UserDto;
import org.ecommerce.userapi.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    @PostMapping("/general")
    public ResponseEntity<?> generalRegistration(@RequestBody UserDto.Request.Register register) {
        return userService.createUser(register);
    }

}