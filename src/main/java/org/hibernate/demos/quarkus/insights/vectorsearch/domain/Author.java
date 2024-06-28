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
	public Long id;

	@Column(length = 512)
	@FullTextField(analyzer = "index", searchAnalyzer = "search", highlightable = Highlightable.FAST_VECTOR)
	public String name;

}
