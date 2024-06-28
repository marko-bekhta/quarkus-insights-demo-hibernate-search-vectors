package org.hibernate.demos.quarkus.insights.vectorsearch.search;

import org.hibernate.search.mapper.pojo.bridge.ValueBridge;
import org.hibernate.search.mapper.pojo.bridge.runtime.ValueBridgeToIndexedValueContext;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.AllMiniLmL6V2QuantizedEmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TextEmbeddingModelBridge implements ValueBridge<String, float[]> {
	public static final int DIMENSION = 384;

	EmbeddingModel embeddingModel = new AllMiniLmL6V2QuantizedEmbeddingModel();

	@Override
	public float[] toIndexedValue(String sentence, ValueBridgeToIndexedValueContext context) {
		return toEmbedding( sentence );
	}

	public float[] toEmbedding(String text) {
		Embedding embedding = embeddingModel.embed( text ).content();
		return embedding.vector();
	}
}
