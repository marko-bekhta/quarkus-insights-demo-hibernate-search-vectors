package org.hibernate.demos.quarkus.insights.vectorsearch.dto;

public record AuthorDto(
		Long id,
		String name
) implements Identifiable<Long> {
}
