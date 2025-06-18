package org.tech.technnicaltask.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tech.technnicaltask.dto.TaskDto;
import org.tech.technnicaltask.dto.TaskUpdateDto;
import org.tech.technnicaltask.service.TaskService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tasks")
@Slf4j
@RequiredArgsConstructor
public class TaskRestControllerV1 {

	private final TaskService taskService;

	@GetMapping("/{id}")
	public ResponseEntity<TaskDto> getById(@PathVariable UUID id) {
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(taskService.getById(id));
	}

	@GetMapping()
	public ResponseEntity<List<TaskDto>> getAll() {
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(taskService.getAllTasks());
	}

	@PostMapping()
	public ResponseEntity<TaskDto> saveTask(@RequestBody @Valid TaskDto dto) {
		return ResponseEntity
				.status(HttpStatus.CREATED)
				.body(taskService.save(dto));
	}

	@PutMapping("/{id}")
	public ResponseEntity<TaskDto> updateTaskTitle(@PathVariable UUID id, @RequestBody @Valid TaskUpdateDto updateDto) {
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(taskService.updateTask(id, updateDto));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteTask(@PathVariable UUID id) {
		taskService.deleteById(id);
		return ResponseEntity
				.status(HttpStatus.NO_CONTENT)
				.body("Successfully deleted");
	}
}
