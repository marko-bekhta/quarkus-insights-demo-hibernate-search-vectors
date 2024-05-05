package org.hibernate.demos.quarkus.insights.vectorsearch.search;

import java.nio.file.Path;

import org.hibernate.search.mapper.pojo.bridge.ValueBridge;
import org.hibernate.search.mapper.pojo.bridge.runtime.ValueBridgeFromIndexedValueContext;
import org.hibernate.search.mapper.pojo.bridge.runtime.ValueBridgeToIndexedValueContext;

public class PathBridge implements ValueBridge<Path, String> {
	@Override
	public String toIndexedValue(Path path, ValueBridgeToIndexedValueContext valueBridgeToIndexedValueContext) {
		return path == null ? null : path.toString();
	}

	@Override
	public Path fromIndexedValue(String value, ValueBridgeFromIndexedValueContext context) {
		return value == null ? null : Path.of( value );
	}
}
