package org.tech.technnicaltask.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.tech.technnicaltask.dto.TaskDto;
import org.tech.technnicaltask.dto.TaskUpdateDto;
import org.tech.technnicaltask.entity.TaskEntity;
import org.tech.technnicaltask.exceptions.BadRequestException;
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

	private TaskUpdateDto defaulttaskUpdateDto;

	@BeforeEach
	public void init() {
		defaultTaskEntity = createDefaultTaskEntity();
		defaultTaskDto = createDefaultTaskDto();
		defaulttaskUpdateDto = createDefaultTaskUpdateDto();

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

	@Test
	public void deleteById_WithValidId_DeletesTask() {
		doNothing().when(taskRepository).delete(any());
		when(taskRepository.findById(any())).thenReturn(Optional.ofNullable(defaultTaskEntity));

		taskService.deleteById(defaultTaskDto.getId());
		verify(taskRepository,times(1)).delete(defaultTaskEntity);
	}

	@ParameterizedTest
	@NullSource
	public void deleteById_WithNullId_ThrowsBadRequestException(UUID id) {
		assertThrows(BadRequestException.class, () -> taskService.deleteById(id));
		verify(taskRepository, times(0)).delete(any());
	}

	@Test
	public void deleteById_WithInvalidId_ThrowsNotFoundException() {
		//Suppose this id doesn't exist
		when(taskRepository.findById(defaultTaskDto.getId())).thenThrow(new TaskNotFoundException(ErrorCode.TASK_NOT_FOUND.getMessage()));

		Exception exception = assertThrows(TaskNotFoundException.class, () -> taskService.deleteById(defaultTaskDto.getId()));
		assertEquals(ErrorCode.TASK_NOT_FOUND.getMessage(), exception.getMessage());
	}

	@Test
	public void updateTask_WithValidData_ReturnsUpdatedDto() {
		when(taskRepository.findById(any())).thenReturn(Optional.ofNullable(defaultTaskEntity));
		when(taskRepository.save(any())).thenReturn(defaultTaskEntity);

		TaskDto expected = defaultTaskDto;
		expected.setTitle(defaultTaskDto.getTitle());
		expected.setDescription(defaulttaskUpdateDto.description());
		expected.setStatus(defaulttaskUpdateDto.status());

		assertEquals(expected, taskService.updateTask(defaultTaskDto.getId(), defaulttaskUpdateDto));
		verify(taskRepository, times(1)).findById(defaultTaskDto.getId());
		verify(taskRepository, times(1)).save(defaultTaskEntity);
	}

	@ParameterizedTest
	@NullSource
	public void updateTask_WithNullTaskUpdateDto_ThrowsBadRequestException(TaskUpdateDto dto) {
		Exception exception = assertThrows(BadRequestException.class, () -> taskService.updateTask(defaultTaskDto.getId(), dto));
		assertEquals(ErrorCode.NULL_UPDATE_DTO.getMessage(), exception.getMessage());
		verify(taskRepository, times(0)).findById(any());
		verify(taskRepository, times(0)).save(any());
	}

	@ParameterizedTest
	@NullSource
	public void updateTask_WithNullId_ThrowsBadRequestException(UUID id) {
		Exception exception = assertThrows(BadRequestException.class, () -> taskService.updateTask(id, defaulttaskUpdateDto));
		assertEquals(ErrorCode.ILLEGAL_MODIFY_ARGUMENTS.getMessage(), exception.getMessage());
		verify(taskRepository, times(0)).findById(any());
		verify(taskRepository, times(0)).save(any());
	}

	@Test
	public void updateTask_WithInvalidId_ThrowsTaskNotFoundException() {
		//Suppose this id doesn't exist
		when(taskRepository.findById(defaultTaskDto.getId())).thenThrow(new TaskNotFoundException(ErrorCode.TASK_NOT_FOUND.getMessage()));

		Exception exception = assertThrows(TaskNotFoundException.class, () -> taskService.deleteById(defaultTaskDto.getId()));
		assertEquals(ErrorCode.TASK_NOT_FOUND.getMessage(), exception.getMessage());
		verify(taskRepository, times(1)).findById(any());
		verify(taskRepository, times(0)).save(any());
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

	private TaskUpdateDto createDefaultTaskUpdateDto() {
		return new TaskUpdateDto("ValidTitle", "Description", "COMPLETED");
	}

}
