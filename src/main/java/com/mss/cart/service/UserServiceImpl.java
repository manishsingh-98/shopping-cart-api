package com.mss.cart.service;

import com.mss.cart.config.JwtTokenProvider;
import com.mss.cart.dto.CreateUserRequestDto;
import com.mss.cart.dto.LoginRequestDto;
import com.mss.cart.dto.LoginResponseDto;
import com.mss.cart.entity.User;
import com.mss.cart.exception.ErrorResponseException;
import com.mss.cart.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private AuthenticationManager authenticationManager;

    private JwtTokenProvider jwtTokenProvider;

    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(AuthenticationManager authenticationManager, UserRepository userRepository, JwtTokenProvider jwtTokenProvider, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public LoginResponseDto userLogin(LoginRequestDto loginRequestDto) {
        String username = loginRequestDto.getUsername().trim();
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, loginRequestDto.getPassword()));
        String token = jwtTokenProvider.createToken(username, this.userRepository.findByUsername(username).orElseThrow(() -> new ErrorResponseException("Username " + username + "not found")).getRole());
        return new LoginResponseDto(token, "", username);
    }

    @Override
    public void createUser(CreateUserRequestDto createUserRequestDto) {
        User user = User.builder().username(createUserRequestDto.getEmail())
                .role(createUserRequestDto.getRole())
                .isEnabled(true)
                .password(passwordEncoder.encode(createUserRequestDto.getPassword())).build();

        userRepository.save(user);
    }

    @Override
    public void disableUser(String username, Boolean isEnabled) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ErrorResponseException(("no such user exists")));
        user.setIsEnabled(isEnabled);
        userRepository.save(user);
    }
}
