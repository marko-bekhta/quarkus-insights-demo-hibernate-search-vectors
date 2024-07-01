package org.hibernate.demos.quarkus.insights.vectorsearch.dto;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.demos.quarkus.insights.vectorsearch.domain.Genre;
import org.hibernate.search.engine.search.common.ValueConvert;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FieldProjection;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.HighlightProjection;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IdProjection;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.ProjectionConstructor;

public record BookDto(
		Long id,
		String title,
		String summary,
		AuthorDto author,
		String coverLocation,
		Set<Genre> genres
) implements Identifiable<Long> {
	@ProjectionConstructor
	public BookDto(
			@IdProjection Long id,
			@HighlightProjection(path = "title") List<String> title,
			@HighlightProjection(path = "summary") List<String> summary,
			@FieldProjection(path = "author.name", convert = ValueConvert.NO) String author,
			@FieldProjection(convert = ValueConvert.NO) String coverLocation,
			@FieldProjection List<Genre> genres) {
		this( id, title.get( 0 ), summary.get( 0 ), new AuthorDto( null, author ),
				coverLocation, new HashSet<>( genres )
		);
	}

	public BookDto(String title, String summary, Set<Genre> genres, AuthorDto author) {
		this( null, title, summary, author, null, genres );
	}
}
