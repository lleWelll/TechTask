package org.tech.technnicaltask.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
	public TaskDto getById(@PathVariable UUID id) {
		return taskService.getById(id);
	}

	@GetMapping()
	public List<TaskDto> getAll() {
		return taskService.getAllTasks();
	}

	@PostMapping()
	public TaskDto saveTask(@RequestBody @Valid TaskDto dto) {
		return taskService.save(dto);
	}

	@PutMapping("/{id}")
	public TaskDto updateTaskTitle(@PathVariable UUID id, @RequestBody @Valid TaskUpdateDto updateDto) {
		return taskService.updateTask(id, updateDto);
	}

	@DeleteMapping("/{id}")
	public void deleteTask(@PathVariable UUID id) {
		taskService.deleteById(id);
	}
}
