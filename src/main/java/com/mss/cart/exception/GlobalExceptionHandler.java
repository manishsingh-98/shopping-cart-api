package com.mss.cart.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	private static ObjectMapper objectMapper = new ObjectMapper();

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		Map<String, Object> responseMap = new HashMap<>();

		List<String> errors = ex.getBindingResult().getFieldErrors().stream()
				.map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
		responseMap.put("errorList", errors);

		try {
			return new ResponseEntity<>("Validation failed \n" + objectMapper.writeValueAsString(responseMap),
					HttpStatus.BAD_REQUEST);
		} catch (JsonProcessingException e) {
			return new ResponseEntity<>("Validation failed",
					HttpStatus.BAD_REQUEST);
		}
	}

	@ExceptionHandler(ErrorResponseException.class)
	public ResponseEntity<Object> errorResponseExceptionHandler(ErrorResponseException ex) {
		return new ResponseEntity<>(ex.getMessage(), ex.getStatus());
	}


}
