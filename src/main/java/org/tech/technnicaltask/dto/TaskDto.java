package org.tech.technnicaltask.dto;

import lombok.Builder;
import lombok.Data;
import jakarta.validation.constraints.*;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class TaskDto {

	//This field can be null
	private UUID id;

	@NotEmpty(message = "title can't be null or empty")
	private String title;

	//This field can be null
	private String description;

	@NotNull(message = "status is required")
	private String status;

	//This field can be null
	private LocalDateTime createdAt;

	//This field can be null
	private LocalDateTime updatedAt;
}
