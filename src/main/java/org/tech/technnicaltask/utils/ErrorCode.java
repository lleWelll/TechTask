package org.tech.technnicaltask.utils;

import lombok.Getter;

@Getter
public enum ErrorCode {

	EMPTY_TASKS_LIST("No tasks available"),

	TASK_NOT_FOUND("Task with id '%s' not found"),

	NULL_UPDATE_DTO("Update payload must not be null"),

	ILLEGAL_MODIFY_ARGUMENTS("Task id and modifier function must not be null"),

	INVALID_UUID("Invalid UUID: %s");


	private final String message;

	ErrorCode(String message) {
		this.message = message;
	}

	public String getFormattedMessage(Object... args) {
		return String.format(message, args);
	}
}
