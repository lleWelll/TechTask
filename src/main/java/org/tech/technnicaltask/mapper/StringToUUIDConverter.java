package org.tech.technnicaltask.mapper;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class StringToUUIDConverter implements Converter<String, UUID> {
	@Override
	public UUID convert(String source) {
		try {
			return UUID.fromString(source);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Invalid UUID: " + source, e);
		}
	}
}
