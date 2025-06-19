package org.tech.technnicaltask.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.tech.technnicaltask.dto.TaskDto;
import org.tech.technnicaltask.entity.TaskEntity;
import org.tech.technnicaltask.exceptions.TaskNotFoundException;
import org.tech.technnicaltask.mapper.TaskMapper;
import org.tech.technnicaltask.repository.TaskRepository;
import org.tech.technnicaltask.service.TaskService;
import org.tech.technnicaltask.utils.ErrorCode;
import org.tech.technnicaltask.utils.Status;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

	@Mock
	private TaskRepository taskRepository;

	@Mock
	private TaskMapper mapper;

	@InjectMocks
	private TaskService taskService;

	private TaskDto defaultTaskDto;

	private TaskEntity defaultTaskEntity;

	@BeforeEach
	public void init() {
		defaultTaskEntity = createDefaultTaskEntity();
		defaultTaskDto = createDefaultTaskDto();

		lenient().when(mapper.toEntity(any())).thenReturn(defaultTaskEntity);
		lenient().when(mapper.toDtoList(any())).thenReturn(List.of(defaultTaskDto));
		lenient().when(mapper.toDto(any())).thenReturn(defaultTaskDto);
	}

	@Test
	public void getById_withValidId_ReturnsTaskDto() {
		when(taskRepository.findById(defaultTaskDto.getId())).thenReturn(Optional.ofNullable(defaultTaskEntity));

		TaskDto result = taskService.getById(defaultTaskDto.getId());

		assertEquals(defaultTaskDto, result);

		verify(taskRepository, times(1)).findById(any());
	}

	@ParameterizedTest
	@NullSource
	public void getById_WithNullId_ThrowsTaskNotFound(UUID id) {
		when(taskRepository.findById(id)).thenThrow(new TaskNotFoundException());

		assertThrows(TaskNotFoundException.class, () -> taskService.getById(id));
		verify(taskRepository, times(1)).findById(any());
	}

	@Test
	public void getAllTasks_ReturnsListOfDto() {
		when(taskRepository.findAll()).thenReturn(List.of(defaultTaskEntity));

		List<TaskDto> result = taskService.getAllTasks();

		assertEquals(List.of(defaultTaskDto), result);
		verify(taskRepository, times(1)).findAll();
	}

	@Test
	public void getAllTasks_WhenEntityListEmpty_ThrowsTaskNotFound() {
		when(taskRepository.findAll()).thenThrow(new TaskNotFoundException(ErrorCode.EMPTY_TASKS_LIST.getMessage()));

		Exception exception = assertThrows(TaskNotFoundException.class, () -> taskService.getAllTasks());
		assertEquals(ErrorCode.EMPTY_TASKS_LIST.getMessage(), exception.getMessage());
		verify(taskRepository, times(1)).findAll();
	}

	@Test
	public void save_WithValidTaskDto_ReturnsSavedDto() {
		when(taskRepository.save(defaultTaskEntity)).thenReturn(defaultTaskEntity);

		TaskDto result = taskService.save(defaultTaskDto);
		assertEquals(defaultTaskDto, result);
		verify(taskRepository, times(1)).save(defaultTaskEntity);
	}

	@ParameterizedTest
	@NullSource
	public void save_WithNullTaskDto_ThrowsBadRequestException(TaskDto dto) {

	}










	private TaskDto createDefaultTaskDto() {
		return TaskDto.builder()
				.id(UUID.fromString("f4befda1-dbe2-425d-a52e-939d70d259ba"))
				.title("Default Task")
				.description("It's test task")
				.status(Status.PENDING.toString())
				.build();
	}

	private TaskEntity createDefaultTaskEntity() {
		return TaskEntity.builder()
				.id(UUID.fromString("f4befda1-dbe2-425d-a52e-939d70d259ba"))
				.title("Default Task")
				.description("It's test task")
				.status(Status.PENDING)
				.build();
	}

}
