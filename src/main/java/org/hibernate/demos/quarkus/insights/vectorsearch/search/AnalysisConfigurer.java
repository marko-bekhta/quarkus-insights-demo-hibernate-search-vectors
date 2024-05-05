package org.hibernate.demos.quarkus.insights.vectorsearch.search;

import org.hibernate.search.backend.elasticsearch.analysis.ElasticsearchAnalysisConfigurationContext;
import org.hibernate.search.backend.elasticsearch.analysis.ElasticsearchAnalysisConfigurer;

import io.quarkus.hibernate.search.orm.elasticsearch.SearchExtension;

@SearchExtension
public class AnalysisConfigurer implements ElasticsearchAnalysisConfigurer {
	@Override
	public void configure(ElasticsearchAnalysisConfigurationContext context) {
		context.tokenFilter( "autocomplete" )
				.type( "edge_ngram" )
				.param( "min_gram", 2 )
				.param( "max_gram", 50 );
		context.analyzer( "index" )
				.custom()
				.tokenizer( "standard" )
				.tokenFilters(
						"lowercase",
						"stemmer",
						"stop",
						"asciifolding",
						"autocomplete"
				);
		context.analyzer( "search" )
				.custom()
				.tokenizer( "standard" )
				.tokenFilters(
						"lowercase",
						"stemmer",
						"stop",
						"asciifolding"
				);
	}
}
