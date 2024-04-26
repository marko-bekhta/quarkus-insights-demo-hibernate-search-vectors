package org.hibernate.demos.quarkus.insights.vectorsearch.domain;

import java.nio.file.Path;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PathAttributeConverter implements AttributeConverter<Path, String> {
	@Override
	public String convertToDatabaseColumn(Path attribute) {
		return attribute == null ? null : attribute.toString();
	}

	@Override
	public Path convertToEntityAttribute(String dbData) {
		return dbData == null ? null : Path.of( dbData );
	}
}
