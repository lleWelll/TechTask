package org.tech.technnicaltask.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TaskUpdateDto(
		@Pattern(regexp = "^(?!\\s*$).+$", message = "title can't empty")
		@Size(max = 100)
		String title,
		String description,
		String status) {
	@Override
	public String title() {
		return title;
	}

	@Override
	public String description() {
		return description;
	}

	@Override
	public String status() {
		return status;
	}
}
