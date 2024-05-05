package org.hibernate.demos.quarkus.insights.vectorsearch.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Author {

	@Id
	@GeneratedValue
	private Long id;

	@Column(length = 512)
	private String name;

	public Long getId() {
		return id;
	}

	public Author setId(Long id) {
		this.id = id;
		return this;
	}

	public String getName() {
		return name;
	}

	public Author setName(String name) {
		this.name = name;
		return this;
	}
}
