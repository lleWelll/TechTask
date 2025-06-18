package org.tech.technnicaltask.mapper;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.tech.technnicaltask.exceptions.BadRequestException;
import org.tech.technnicaltask.utils.ErrorCode;

import java.util.UUID;

@Component
public class StringToUUIDConverter implements Converter<String, UUID> {
	@Override
	public UUID convert(String source) {
		try {
			return UUID.fromString(source);
		} catch (IllegalArgumentException e) {
			throw new BadRequestException(ErrorCode.INVALID_UUID.getFormattedMessage(source), e);
		}
	}
}
