package org.tech.technnicaltask.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.tech.technnicaltask.dto.TaskDto;
import org.tech.technnicaltask.entity.TaskEntity;
import org.tech.technnicaltask.utils.Status;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TaskMapper {

	@Mapping(source = "status", target = "status", qualifiedByName = "mapStringToStatus")
	TaskEntity toEntity(TaskDto dto);

	TaskDto toDto(TaskEntity entity);

	List<TaskEntity> toEntityList(List<TaskDto> dtos);

	List<TaskDto> toDtoList(List<TaskEntity> entities);

	@Named("mapStringToStatus")
	static Status toStatus(String status) {
		return Status.valueOf(status.toUpperCase());
	}
}
