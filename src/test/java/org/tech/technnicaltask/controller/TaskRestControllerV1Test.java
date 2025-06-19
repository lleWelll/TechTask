package org.tech.technnicaltask.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.tech.technnicaltask.dto.TaskDto;
import org.tech.technnicaltask.dto.TaskUpdateDto;
import org.tech.technnicaltask.exceptions.GlobalExceptionHandler;
import org.tech.technnicaltask.exceptions.TaskNotFoundException;
import org.tech.technnicaltask.service.TaskService;
import org.tech.technnicaltask.utils.ErrorCode;
import org.tech.technnicaltask.utils.Status;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class TaskRestControllerV1Test {

	@Mock
	private TaskService taskService;

	@InjectMocks
	private TaskRestControllerV1 controller;

	private MockMvc mockMVC;

	private TaskDto defaultDto;

	private TaskUpdateDto defaultUpdateDto;

	private ObjectMapper mapper;

	@BeforeEach
	public void init() {
		defaultDto = createDefaultTaskDto();
		defaultUpdateDto = createDefaultTaskUpdateDto();
		mockMVC = createMockMvc();
		mapper = new ObjectMapper();
	}

	@Test
	public void getById_WithValidId_ReturnsOKHttpStatus() throws Exception {
		when(taskService.getById(defaultDto.getId())).thenReturn(defaultDto);

		mockMVC.perform(get("/api/v1/tasks/{id}", "f4befda1-dbe2-425d-a52e-939d70d259ba"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.title").value(defaultDto.getTitle()))
				.andExpect(jsonPath("$.description").value(defaultDto.getDescription()))
				.andExpect(jsonPath("$.status").value(defaultDto.getStatus()));

		verify(taskService, times(1)).getById(defaultDto.getId());
	}

	@Test
	public void getById_WithInvalidId_ReturnsNotFoundResponse() throws Exception {
		//Mocking method getById(), when any UUID is argument, method throws TaskNotFoundException with formatted message
		//suppose that task with this id does not exist
		when(taskService.getById(defaultDto.getId())).thenAnswer(invocation -> {
			UUID id = invocation.getArgument(0);
			throw new TaskNotFoundException(ErrorCode.TASK_NOT_FOUND.getFormattedMessage(id));
		});

		//Checking that exception was handled right
		mockMVC.perform(get("/api/v1/tasks/{id}", defaultDto.getId())) //suppose that task with this id does not exist
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.httpStatusCode").value(404)) //Checking returned status
				.andExpect(jsonPath("$.exception").value("TaskNotFoundException")) //Checking returned exception type
				.andExpect(jsonPath("$.message")
						.value(ErrorCode.TASK_NOT_FOUND.getFormattedMessage(defaultDto.getId()))); //Checking returned message

		verify(taskService, times(1)).getById(any());
	}

	//Checking that getAllTasks method returns right list of dtos with right status code
	@Test
	public void getAllTasks_ReturnsOkAndListOfDto() throws Exception {
		when(taskService.getAllTasks()).thenReturn(List.of(defaultDto));

		mockMVC.perform(get("/api/v1/tasks"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(1))
				.andExpect(jsonPath("$[0].id").value(defaultDto.getId().toString()));

		verify(taskService, times(1)).getAllTasks();
	}

	//Testing that returns 404 status code with right exception message when there are no tasks in DB
	@Test
	public void getAllTasks_ReturnsNotFoundStatus() throws Exception {
		when(taskService.getAllTasks()).thenThrow(new TaskNotFoundException(ErrorCode.EMPTY_TASKS_LIST.getMessage()));

		mockMVC.perform(get("/api/v1/tasks"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.httpStatusCode").value(404))
				.andExpect(jsonPath("$.exception").value("TaskNotFoundException"))
				.andExpect(jsonPath("$.message").value(ErrorCode.EMPTY_TASKS_LIST.getMessage()));

		verify(taskService, times(1)).getAllTasks();
	}

	//Testing that controller successfully saves new valid task
	@ParameterizedTest
	@MethodSource("generateValidDtos")
	public void saveTask_WithValidData_ReturnOkAndSavedDto(TaskDto valid) throws Exception {
		String dtoJson = mapper.writeValueAsString(valid);

		TaskDto expected = valid;
		expected.setId(defaultDto.getId()); //This id is added because, after saving in db service return saved entity with automatically assigned id
		when(taskService.save(any())).thenReturn(expected);

		mockMVC.perform(post("/api/v1/tasks").contentType(MediaType.APPLICATION_JSON).content(dtoJson))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(expected.getId().toString()));
		verify(taskService, times(1)).save(any());
	}

	@ParameterizedTest
	@MethodSource("generateInvalidDtos")
	void saveTask_WithInvalidData_ReturnsBadRequestHttpStatus(TaskDto invalid) throws Exception {
		String dtoJson = mapper.writeValueAsString(invalid);

		mockMVC.perform(post("/api/v1/tasks").contentType(MediaType.APPLICATION_JSON).content(dtoJson))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.exception").value("MethodArgumentNotValidException"))
				.andExpect(jsonPath("$.message").exists());
	}

	@ParameterizedTest
	@NullSource
	void saveTask_WithNullBody_ReturnsBadRequestHttpStatus(TaskDto dto) throws Exception {
		String jsonDto = mapper.writeValueAsString(dto);
		mockMVC.perform(post("/api/v1/tasks").contentType(MediaType.APPLICATION_JSON).content(jsonDto))
				.andExpect(status().isBadRequest());

		verify(taskService, times(0)).save(defaultDto);
	}

	@Test
	public void updateTask_WithValidUpdateDto_ReturnsOkAndUpdatedDto() throws Exception {
		TaskDto expected = defaultDto; //suppose this object is correctly modified TaskDto

		String jsonDto = mapper.writeValueAsString(defaultUpdateDto);

		when(taskService.updateTask(defaultDto.getId(), defaultUpdateDto)).thenReturn(expected);

		mockMVC.perform(put("/api/v1/tasks/{id}", defaultDto.getId()).contentType(MediaType.APPLICATION_JSON).content(jsonDto))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(expected.getId().toString()))
				.andExpect(jsonPath("$.title").value(expected.getTitle()))
				.andExpect(jsonPath("$.description").value(expected.getDescription()))
				.andExpect(jsonPath("$.status").value(expected.getStatus()));

		verify(taskService, times(1)).updateTask(defaultDto.getId(), defaultUpdateDto);
	}

	@Test
	public void updateTask_WithInvalidUpdateDto_ReturnsBadRequestStatus() throws Exception {
		TaskUpdateDto invalid = new TaskUpdateDto("", "Desc", "PENDING");
		String jsonDto = mapper.writeValueAsString(invalid);

		mockMVC.perform(put("/api/v1/tasks/{id}", defaultDto.getId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonDto))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.exception").value("MethodArgumentNotValidException"));
	}

	@Test
	public void updateTask_WithNullUpdateDto_ReturnsBadRequest() throws Exception {
		String jsonDto = mapper.writeValueAsString(null);

		mockMVC.perform(put("/api/v1/tasks/{id}", defaultDto.getId()).contentType(MediaType.APPLICATION_JSON).content(jsonDto))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void updateTask_WithInvalidId_ReturnsBadRequest() throws Exception {
		UUID invalidId = defaultDto.getId(); //Suppose this uuid is not valid
		String jsonDto = mapper.writeValueAsString(defaultUpdateDto);

		when(taskService.updateTask(invalidId, defaultUpdateDto)).thenThrow(new TaskNotFoundException(ErrorCode.TASK_NOT_FOUND.getMessage()));

		mockMVC.perform(put("/api/v1/tasks/{id}", invalidId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonDto))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.exception").value("TaskNotFoundException"))
				.andExpect(jsonPath("$.message").exists());
	}

	@Test
	public void deleteTask_WithValidId_ReturnNoContentStatus() throws Exception {
		Mockito.doNothing().when(taskService).deleteById(defaultDto.getId());

		mockMVC.perform(delete("/api/v1/tasks/{id}", defaultDto.getId()))
				.andExpect(status().isNoContent());
	}

	@Test
	public void deleteTask_WithInvalidId_ReturnNotFound() throws Exception {
		Mockito.doThrow(new TaskNotFoundException(ErrorCode.TASK_NOT_FOUND.getMessage())).when(taskService).deleteById(defaultDto.getId());

		mockMVC.perform(delete("/api/v1/tasks/{id}", defaultDto.getId()))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.exception").value("TaskNotFoundException"))
				.andExpect(jsonPath("$.message").exists());
	}

	private static TaskDto createDefaultTaskDto() {
		return TaskDto.builder()
				.id(UUID.fromString("f4befda1-dbe2-425d-a52e-939d70d259ba"))
				.title("Default Task")
				.description("It's test task")
				.status(Status.PENDING.toString())
				.build();
	}

	private static TaskUpdateDto createDefaultTaskUpdateDto() {
		return new TaskUpdateDto("ValidTitle", "Description", "COMPLETED");
	}

	//initialization of MockMVC for testing returning http status codes and exception handling
	private MockMvc createMockMvc() {
		return MockMvcBuilders
				.standaloneSetup(controller)
				.setControllerAdvice(new GlobalExceptionHandler()) //Adding ExceptionHandler to test exception handling
				.build();
	}

	private static List<TaskDto> generateInvalidDtos() {
		//Creating list of consumers that will change dtos
		List<Consumer<TaskDto>> modifiers = List.of(
				dto -> dto.setTitle(null),
				dto -> dto.setTitle(""),
				dto -> dto.setTitle(" "),
				dto -> dto.setTitle("a".repeat(101)),
				dto -> dto.setStatus(null)
		);

		return applyModifiers(modifiers);
	}

	private static List<TaskDto> generateValidDtos() {
		//Creating list of consumers that will change dtos
		List<Consumer<TaskDto>> modifiers = List.of(
				dto -> dto.setId(null),
				dto -> dto.setTitle("Valid Name"),
				dto -> dto.setTitle(" Valid Name "),
				dto -> dto.setStatus("pending"),
				dto -> dto.setStatus("PeNdInG"),
				dto -> dto.setStatus("IN_PROGRESS"),
				dto -> dto.setStatus("COMPLETED")
		);

		return applyModifiers(modifiers);
	}

	//creating list of modified dtos
	private static List<TaskDto> applyModifiers(List<Consumer<TaskDto>> modifiers) {
		return  modifiers.stream()
				.map(modifier -> {
					TaskDto copy = createDefaultTaskDto();
					modifier.accept(copy);
					return copy;
				})
				.toList();
	}
}
