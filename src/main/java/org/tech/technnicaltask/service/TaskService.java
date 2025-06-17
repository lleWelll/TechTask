package org.tech.technnicaltask.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.tech.technnicaltask.dto.TaskDto;
import org.tech.technnicaltask.dto.TaskUpdateDto;
import org.tech.technnicaltask.entity.TaskEntity;
import org.tech.technnicaltask.exceptions.TaskNotFoundException;
import org.tech.technnicaltask.mapper.TaskMapper;
import org.tech.technnicaltask.repository.TaskRepository;
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
		log.info("Returning task with id {}", id);
		return mapper.toDto(entity);
	}

	//Getting all Tasks from DB
	public List<TaskDto> getAllTasks() {
		log.info("Getting all tasks");
		List<TaskEntity> entities = taskRepository.findAll();
		if (entities.isEmpty()) {
			log.warn("There are no tasks");
			throw new TaskNotFoundException("There are no tasks");
		}
		return mapper.toDtoList(entities);
	}

	//Saving Task to DB
	public TaskDto save(TaskDto dto) {
		if (dto.getId() != null) {
			dto.setId(null);
		}
		log.info("Saving task: {}", dto);
		TaskEntity entity = mapper.toEntity(dto);
		TaskEntity savedEntity = taskRepository.save(entity);
		log.info("Task saved successfully");
		return mapper.toDto(savedEntity);
	}

	//Deleting Task from DB. If it's not found it is silently ignored
	public void deleteById(UUID id) {
		log.info("Deleting task with id {}", id.toString());
		taskRepository.deleteById(id);
	}

	//Updating task. If field in TaskUpdateDto != null, this field will be changed in entity
	public TaskDto updateTask(UUID id, TaskUpdateDto updateDto) {
		if (updateDto == null) {
			throw new IllegalArgumentException("Provided updateDto is null");
		}
		return modifyTask(id, entity -> {
			//selecting fields that need to change (multiple fields can be changed)
			if (updateDto.title() != null) entity.setTitle(updateDto.title());
			if (updateDto.description() != null) entity.setDescription(updateDto.description());
			if (updateDto.status() != null) entity.setStatus(Status.valueOf(updateDto.status().toUpperCase()));
		});
	}

	//Modifies Task in DB. Uses Consumer to update the state of task and saves object
	private TaskDto modifyTask(UUID id, Consumer<TaskEntity> modifyFunction) {
		if (id == null || modifyFunction == null) {
			throw new IllegalArgumentException("Illegal arguments to modify task");
		}
		TaskEntity entity = getEntityById(id);
		modifyFunction.accept(entity); //Modifying entity's state
		TaskEntity savedEntity = taskRepository.save(entity);
		log.info("Task modified and saved successfully");
		return mapper.toDto(savedEntity);
	}

	//Getting from DB TaskEntity or throw TaskNotFoundException if task is not exists in
	private TaskEntity getEntityById(UUID id) {
		log.info("Getting entity with id {}", id.toString());
		return taskRepository.findById(id).orElseThrow(
				() -> new TaskNotFoundException("Task with id " + id + " is not found")
		);
	}
}
