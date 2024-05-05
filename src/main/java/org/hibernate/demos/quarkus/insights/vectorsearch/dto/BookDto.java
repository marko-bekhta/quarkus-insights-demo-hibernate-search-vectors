package org.hibernate.demos.quarkus.insights.vectorsearch.dto;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.demos.quarkus.insights.vectorsearch.domain.Book;
import org.hibernate.demos.quarkus.insights.vectorsearch.domain.Genre;
import org.hibernate.search.engine.search.common.ValueConvert;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FieldProjection;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.HighlightProjection;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IdProjection;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.ProjectionConstructor;

public class BookDto extends Identifiable<Long> {

	private String title;

	private String summary;

	private Identifiable<Long> author;

	private Set<Genre> genres;

	private String coverLocation;

	public BookDto() {
	}

	public BookDto(Book book) {
		this.id = book.getId();
		this.title = book.getTitle();
		this.summary = book.getSummary();
		this.genres = book.getGenres();
		this.coverLocation = book.getCoverLocation().getFileName().toString();
		this.author = new AuthorDto( book.getAuthor() );

	}

	@ProjectionConstructor
	public BookDto(
			@IdProjection Long id,
			@HighlightProjection(path = "title") List<String> title,
			@HighlightProjection(path = "summary") List<String> summary,
			@FieldProjection(convert = ValueConvert.NO) String author,
			@FieldProjection(convert = ValueConvert.NO) String coverLocation,
			@FieldProjection List<Genre> genres) {
		this.id = id;
		this.title = title.get( 0 );
		this.summary = summary.get( 0 );
		this.coverLocation = coverLocation;
		AuthorDto author1 = new AuthorDto();
		author1.setName( author );
		this.author = author1;
		this.genres = new HashSet<>( genres );
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public Identifiable<Long> getAuthor() {
		return author;
	}

	public void setAuthor(Identifiable<Long> author) {
		this.author = author;
	}

	public Set<Genre> getGenres() {
		return genres;
	}

	public void setGenres(Set<Genre> genres) {
		this.genres = genres;
	}

	public String getCoverLocation() {
		return coverLocation;
	}

	public void setCoverLocation(String coverLocation) {
		this.coverLocation = coverLocation;
	}
}
