package org.tech.technnicaltask.dto;

import lombok.Data;
import org.tech.technnicaltask.utils.Status;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TaskDto {

	private UUID id;

	private String title;

	private String description;

	private Status status;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;
}
