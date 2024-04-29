package org.hibernate.demos.quarkus.insights.vectorsearch.domain;

import java.nio.file.Path;
import java.util.Set;

import org.hibernate.search.engine.backend.types.VectorSimilarity;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.VectorField;

import io.quarkus.runtime.configuration.PathConverter;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Indexed
@Entity
public class Book {

	@Id
	@GeneratedValue
	private Long id;

	@FullTextField
	private String title;

	@FullTextField
//	@VectorField(name = "summary_embedding",
//			dimension = EmbeddingModelBridge.DIMENSION,
//			vectorSimilarity = VectorSimilarity.COSINE,
//			valueBridge = @ValueBridgeRef(type = EmbeddingModelBridge.class))
	private String summary;

	@ManyToOne
	private Author author;

	@KeywordField
	@ElementCollection
	private Set<Genre> genres;

	private Path coverLocation;

	@VectorField(dimension = 512, vectorSimilarity = VectorSimilarity.COSINE)
	private float[] coverEmbedding;


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public Author getAuthor() {
		return author;
	}

	public void setAuthor(Author author) {
		this.author = author;
	}

	public Set<Genre> getGenres() {
		return genres;
	}

	public void setGenres(Set<Genre> genres) {
		this.genres = genres;
	}

	public Path getCoverLocation() {
		return coverLocation;
	}

	public void setCoverLocation(Path coverLocation) {
		this.coverLocation = coverLocation;
	}

	public float[] getCoverEmbedding() {
		return coverEmbedding;
	}

	public void setCoverEmbedding(float[] coverEmbedding) {
		this.coverEmbedding = coverEmbedding;
	}
}
