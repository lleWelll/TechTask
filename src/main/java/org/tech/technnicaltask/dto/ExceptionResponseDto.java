package org.tech.technnicaltask.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ExceptionResponseDto(Integer httpStatusCode, String exception, String message, LocalDateTime timestamp, String path) {
}
