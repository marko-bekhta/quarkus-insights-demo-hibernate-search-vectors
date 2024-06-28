package org.hibernate.demos.quarkus.insights.vectorsearch.domain;

import java.nio.file.Path;
import java.util.Set;

import org.hibernate.demos.quarkus.insights.vectorsearch.search.PathBridge;
import org.hibernate.demos.quarkus.insights.vectorsearch.search.TextEmbeddingModelBridge;
import org.hibernate.search.engine.backend.types.Highlightable;
import org.hibernate.search.engine.backend.types.VectorSimilarity;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.bridge.mapping.annotation.ValueBridgeRef;
import org.hibernate.search.mapper.pojo.extractor.mapping.annotation.ContainerExtract;
import org.hibernate.search.mapper.pojo.extractor.mapping.annotation.ContainerExtraction;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.VectorField;

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
	public Long id;

	@FullTextField(analyzer = "index", searchAnalyzer = "search", highlightable = Highlightable.FAST_VECTOR)
	public String title;

	@FullTextField(analyzer = "index", searchAnalyzer = "search", highlightable = Highlightable.FAST_VECTOR)
	@VectorField(name = "summary_embedding",
			dimension = TextEmbeddingModelBridge.DIMENSION,
			vectorSimilarity = VectorSimilarity.COSINE,
			valueBridge = @ValueBridgeRef(type = TextEmbeddingModelBridge.class))
	public String summary;

	@ManyToOne
	@IndexedEmbedded
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	public Author author;

	@KeywordField
	@ElementCollection
	public Set<Genre> genres;

	@KeywordField(extraction = @ContainerExtraction(extract = ContainerExtract.NO), valueBridge = @ValueBridgeRef(type = PathBridge.class))
	public Path coverLocation;

	@VectorField(dimension = 512, vectorSimilarity = VectorSimilarity.COSINE)
	public float[] coverEmbedding;

}
