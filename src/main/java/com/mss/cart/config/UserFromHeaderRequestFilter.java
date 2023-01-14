package com.mss.cart.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;


@Component
public class UserFromHeaderRequestFilter extends OncePerRequestFilter {

	public static final String ID_TOKEN_HEADER = "Bearer ";

	private final JwtTokenProvider jwtTokenProvider;

	@Autowired
	public UserFromHeaderRequestFilter(JwtTokenProvider jwtTokenProvider) {
		this.jwtTokenProvider = jwtTokenProvider;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException {
		try {
			if (!PUBLIC_URLS.matches(request)) {
				//logger.debug("Authorization Header Content: " + request.getHeader(HttpHeaders.AUTHORIZATION));
				final String idToken = request.getHeader(HttpHeaders.AUTHORIZATION).substring(ID_TOKEN_HEADER.length());

				if (!idToken.isEmpty()) {
					jwtTokenProvider.validateToken(idToken);
					final var userContext = jwtTokenProvider.decode(idToken);
					SecurityContextHolder.getContext().setAuthentication(userContext);
				}
			}
		} catch (ErrorResponseException e) {
			logger.info("Token Expired: " + e.getMessage());
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().write(e.getMessage());
			return;
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			String errorMessage = e.getMessage();
			if (Objects.isNull(request.getHeader(HttpHeaders.AUTHORIZATION))) {
				errorMessage = "Authorization header empty";
			}
			logger.info("invalid login token: " + errorMessage);
			response.getWriter().write(Objects.isNull(errorMessage) ? "invalid login request" : errorMessage);
			return;
		}
		try {
			filterChain.doFilter(request, response);
		} catch (IOException | ServletException e) {
			logger.error(e);
		}
	}

	public final RequestMatcher PUBLIC_URLS = new OrRequestMatcher(
		new AntPathRequestMatcher("/user/login"),
		new AntPathRequestMatcher("/user/signup"),
		new AntPathRequestMatcher("/user/refresh-token"),
		new AntPathRequestMatcher("/actuator/**"),
		new AntPathRequestMatcher("/swagger-resources/**"),
		new AntPathRequestMatcher("/configuration/ui"),
		new AntPathRequestMatcher("/configuration/security"),
		new AntPathRequestMatcher("/swagger-ui.html"),
		new AntPathRequestMatcher("/v3/api-docs/**"),
		new AntPathRequestMatcher("/swagger-ui/**")

	);

}
