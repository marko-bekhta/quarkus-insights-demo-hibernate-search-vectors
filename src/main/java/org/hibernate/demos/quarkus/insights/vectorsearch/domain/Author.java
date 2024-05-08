package org.hibernate.demos.quarkus.insights.vectorsearch.domain;

import org.hibernate.search.engine.backend.types.Highlightable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;

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
	@FullTextField(analyzer = "index", searchAnalyzer = "search", highlightable = Highlightable.FAST_VECTOR)
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
