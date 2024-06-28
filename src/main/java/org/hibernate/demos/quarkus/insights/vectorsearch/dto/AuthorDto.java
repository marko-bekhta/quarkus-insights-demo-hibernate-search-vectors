package org.hibernate.demos.quarkus.insights.vectorsearch.dto;

import org.hibernate.demos.quarkus.insights.vectorsearch.domain.Author;

public class AuthorDto extends Identifiable<Long> {

	private String name;

	public AuthorDto() {
	}

	public AuthorDto(Author author) {
		this.id = author.id;
		this.name = author.name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
