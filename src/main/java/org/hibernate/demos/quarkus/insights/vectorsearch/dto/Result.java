package org.hibernate.demos.quarkus.insights.vectorsearch.dto;

import java.util.List;

public record Result<T>(Long total, List<T> hits) {

}
