package org.tech.technnicaltask.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.tech.technnicaltask.dto.ExceptionResponseDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(TaskNotFoundException.class)
	public ExceptionResponseDto handleTaskNotFoundException(TaskNotFoundException e, HttpServletRequest req) {
		log.error(e.getMessage());
		return new ExceptionResponseDto(HttpStatus.NOT_FOUND.value(), e.getClass().getSimpleName(), e.getMessage(), LocalDateTime.now(), req.getRequestURI());
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(BadRequestException.class)
	public ExceptionResponseDto handleBadRequestException(BadRequestException e, HttpServletRequest req) {
		log.error(e.getMessage());
		return new ExceptionResponseDto(HttpStatus.BAD_REQUEST.value(), e.getClass().getSimpleName(), e.getMessage(), LocalDateTime.now(), req.getRequestURI());
	}

	//Handling errors from jakarta.constraints (@NotNull, @NotEmpty)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ExceptionResponseDto handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest req) {
		List<String> defaultMessage = getDefaultMessagesFromMethodArgumentException(e.getBindingResult());
		log.error(defaultMessage.toString());
		return new ExceptionResponseDto(HttpStatus.BAD_REQUEST.value(), e.getClass().getSimpleName(), defaultMessage.toString(), LocalDateTime.now(), req.getRequestURI());
	}

	//Extracting message from jakarta.constraints (@NotNull, @NotEmpty)
	private List<String> getDefaultMessagesFromMethodArgumentException(BindingResult bindingResult) {
		List<String> fieldErrorDefaultMessages = new ArrayList<>();
		for (FieldError fieldError : bindingResult.getFieldErrors()) {
			fieldErrorDefaultMessages.add(fieldError.getDefaultMessage());
		}
		return fieldErrorDefaultMessages;
	}
}
