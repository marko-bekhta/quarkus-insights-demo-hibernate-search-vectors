package org.hibernate.demos.quarkus.insights.vectorsearch.dto;

import java.util.Set;

import org.hibernate.demos.quarkus.insights.vectorsearch.domain.Book;
import org.hibernate.demos.quarkus.insights.vectorsearch.domain.Genre;

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
