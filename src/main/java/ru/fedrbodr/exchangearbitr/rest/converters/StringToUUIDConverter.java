package ru.fedrbodr.exchangearbitr.rest.converters;

import org.springframework.core.convert.converter.Converter;

import java.util.UUID;

public class StringToUUIDConverter implements Converter<String, UUID> {
	public UUID convert(String source) {
		return UUID.fromString(source);
	}
}
