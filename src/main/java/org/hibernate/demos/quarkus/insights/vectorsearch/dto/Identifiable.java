package org.hibernate.demos.quarkus.insights.vectorsearch.dto;

public class Identifiable<I> {
	protected I id;

	public I getId() {
		return id;
	}

	public void setId(I id) {
		this.id = id;
	}
}
