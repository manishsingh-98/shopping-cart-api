package com.mss.cart.config;


import com.mss.cart.entity.User;
import com.mss.cart.exception.ErrorResponseException;
import com.mss.cart.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig {

    @Autowired
    private UserFromHeaderRequestFilter userFromHeaderRequestFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults("");
    }

    @Bean
    AuthenticationManager customAuthenticationManager(UserRepository userRepository, PasswordEncoder encoder) {
        return authentication -> {
            String username = authentication.getPrincipal() + "";
            String password = authentication.getCredentials() + "";

            User user = userRepository.findByUsername(username).orElseThrow(() -> new ErrorResponseException("no such user exists"));

            if (!user.isEnabled()) {
                throw new ErrorResponseException("user disabled by admin");
            }

            if (!encoder.matches(password, user.getPassword())) {
                throw new ErrorResponseException("Bad credentials", HttpStatus.FORBIDDEN);
            }

            return new UsernamePasswordAuthenticationToken(username, null, user.getAuthorities());
        };
    }


    @Bean
    protected SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
        http.csrf()
            .disable()
            .cors()
            .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeHttpRequests()
            .requestMatchers(userFromHeaderRequestFilter.PUBLIC_URLS)
            .permitAll()
            .anyRequest().authenticated()
            .and()
            .addFilterBefore(userFromHeaderRequestFilter, BasicAuthenticationFilter.class);
		return http.build();
    }
}
