package com.mss.cart.service;

import com.mss.cart.dto.CreateUserRequestDto;
import com.mss.cart.dto.LoginRequestDto;
import com.mss.cart.dto.LoginResponseDto;

public interface UserService {

    LoginResponseDto userLogin(LoginRequestDto loginRequestDto);

    void createUser(CreateUserRequestDto createUserRequestDto);

    void disableUser(String username, Boolean isEnabled);

}
