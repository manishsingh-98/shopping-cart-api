package com.mss.cart.dto;

import com.mss.cart.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserRequestDto {

    @NotBlank(message = "email can not be blank")
    private String email;

    @NotBlank(message = "password can not be blank")
    private String password;

    @NotNull(message = "please provide user role")
    private Role role;
}
