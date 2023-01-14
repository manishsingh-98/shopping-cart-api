package com.mss.cart.controller;

import com.mss.cart.dto.CreateUserRequestDto;
import com.mss.cart.dto.LoginRequestDto;
import com.mss.cart.dto.LoginResponseDto;
import com.mss.cart.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public LoginResponseDto userLogin(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        return userService.userLogin(loginRequestDto);
    }

    @PostMapping("/signup")
    public ResponseEntity<String> createUser(@Valid @RequestBody CreateUserRequestDto createUserRequestDto) {
        String message;
        HttpStatus status;
        try {
            userService.createUser(createUserRequestDto);
            message = "user created successfully";
            status = HttpStatus.CREATED;
        } catch (Exception e) {
            message = "error occurred while signing up";
            status = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(message, status);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/disable-user")
    public ResponseEntity<String> disableUser(@RequestParam String username,@Valid @NotNull @RequestParam Boolean isEnabled) {
        HttpStatus status = HttpStatus.OK;
        String message;
        try {
            message = "user disabled successfully";
            userService.disableUser(username, isEnabled);
        } catch (Exception e) {
            message = "error occurred while changing user status";
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(message, status);
    }

}
