package org.hibernate.demos.quarkus.insights.vectorsearch.search;

import org.hibernate.demos.quarkus.insights.vectorsearch.domain.Author;
import org.hibernate.search.mapper.pojo.bridge.ValueBridge;
import org.hibernate.search.mapper.pojo.bridge.runtime.ValueBridgeFromIndexedValueContext;
import org.hibernate.search.mapper.pojo.bridge.runtime.ValueBridgeToIndexedValueContext;

public class AuthorBridge implements ValueBridge<Author, String> {

	@Override
	public String toIndexedValue(Author author, ValueBridgeToIndexedValueContext valueBridgeToIndexedValueContext) {
		return author != null ? author.getName() : null;
	}

	@Override
	public Author fromIndexedValue(String value, ValueBridgeFromIndexedValueContext context) {
		return value == null ? null : new Author().setName( value );
	}
}
