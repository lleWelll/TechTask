package org.tech.technnicaltask.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tech.technnicaltask.dto.TaskDto;
import org.tech.technnicaltask.dto.TaskUpdateDto;
import org.tech.technnicaltask.entity.TaskEntity;
import org.tech.technnicaltask.exceptions.BadRequestException;
import org.tech.technnicaltask.exceptions.TaskNotFoundException;
import org.tech.technnicaltask.mapper.TaskMapper;
import org.tech.technnicaltask.repository.TaskRepository;
import org.tech.technnicaltask.utils.ErrorCode;
import org.tech.technnicaltask.utils.Status;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskService {

	private final TaskRepository taskRepository;

	private final TaskMapper mapper;

	//Getting TaskDTO from DB
	public TaskDto getById(UUID id) {
		TaskEntity entity = getEntityById(id);
		log.info("Successfully fetched Task {} -> {}", id, entity);
		return mapper.toDto(entity);
	}

	//Getting all Tasks from DB
	public List<TaskDto> getAllTasks() {
		List<TaskEntity> entities = taskRepository.findAll();
		if (entities.isEmpty()) {
			log.warn("Task list is empty, throwing TaskNotFoundException");
			throw new TaskNotFoundException(ErrorCode.EMPTY_TASKS_LIST.getMessage());
		}
		log.info("Found {} tasks in database", entities.size());
		return mapper.toDtoList(entities);
	}

	//Saving Task to DB
	public TaskDto save(TaskDto dto) {
		if (dto.getId() != null) {
			log.warn("Provided id ({}), it will be ignored for new entity", dto.getId());
			dto.setId(null);
		}
		log.info("Saving new Task: {}", dto);
		TaskEntity entity = mapper.toEntity(dto);
		TaskEntity savedEntity = taskRepository.save(entity);
		TaskDto result = mapper.toDto(savedEntity);
		log.info("Task saved with generated id={} -> {}", savedEntity.getId(), result);
		return result;
	}

	//Deleting Task from DB. If it's not found it is silently ignored
	public void deleteById(UUID id) {
		if (id == null) {
			log.error("method called with id == null");
			throw new BadRequestException(ErrorCode.INVALID_UUID.getFormattedMessage("null"));
		}
		TaskEntity entity = getEntityById(id);
		taskRepository.delete(entity);
		log.info("Deleted Task with id = {}", id);
	}

	//Updating task. If field in TaskUpdateDto != null, this field will be changed in entity
	@Transactional
	public TaskDto updateTask(UUID id, TaskUpdateDto updateDto) {
		if (updateDto == null) {
			log.info("Deleted Task with id = {}", id);
			throw new BadRequestException(ErrorCode.NULL_UPDATE_DTO.getMessage());
		}
		return modifyTask(id, entity -> {
			//selecting fields that need to change (multiple fields can be changed)
			if (updateDto.title() != null) entity.setTitle(updateDto.title());
			if (updateDto.description() != null) entity.setDescription(updateDto.description());
			if (updateDto.status() != null) entity.setStatus(Status.valueOf(updateDto.status().toUpperCase()));
			log.info("Applying changes to Task(id={}): {} (null values will not be applied)", id, updateDto);
		});
	}

	//Modifies Task in DB. Uses Consumer to update the state of task and saves object
	private TaskDto modifyTask(UUID id, Consumer<TaskEntity> modifyFunction) {
		if (id == null || modifyFunction == null) {
			log.error("modifyTask called with illegal arguments (id={}, function={})", id, modifyFunction);
			throw new BadRequestException(ErrorCode.ILLEGAL_MODIFY_ARGUMENTS.getMessage());
		}
		TaskEntity entity = getEntityById(id);
		log.debug("Loaded Task entity for modification: {}", entity);
		modifyFunction.accept(entity); //Modifying entity's state
		TaskEntity savedEntity = taskRepository.save(entity);
		log.debug("Saved modified Task entity -> {}", savedEntity);
		return mapper.toDto(savedEntity);
	}

	//Getting from DB TaskEntity or throw TaskNotFoundException if task is not exists in
	private TaskEntity getEntityById(UUID id) {
		return taskRepository.findById(id).orElseThrow(
				() -> {
					log.warn("Task not found: id={}", id);
					return new TaskNotFoundException(ErrorCode.TASK_NOT_FOUND.getFormattedMessage(id));
				}
		);
	}
}
