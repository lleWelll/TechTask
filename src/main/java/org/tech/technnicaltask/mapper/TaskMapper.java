package org.tech.technnicaltask.mapper;

import org.mapstruct.Mapper;
import org.tech.technnicaltask.dto.TaskDto;
import org.tech.technnicaltask.entity.TaskEntity;

@Mapper(componentModel = "spring")
public interface TaskMapper {

	TaskEntity toTaskEntity(TaskDto dto);

	TaskDto toTaskDto(TaskEntity entity);
}
