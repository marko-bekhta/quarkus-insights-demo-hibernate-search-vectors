package org.hibernate.demos.quarkus.insights.vectorsearch.domain;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public enum Genre {
	Mystery,
	Fiction,
	Crime,
	Thriller,
	Audiobook,
	Mystery_Thriller,
	Suspense,
	Detective,
	Contemporary,
	Picture_Books,
	Childrens,
	Animals,
	Adventure,
	;

	public static Set<Genre> convert(List<String> genres) {
		Set<Genre> result = new HashSet<>();
		for ( String genre : genres ) {
			try {
				result.add( Genre.valueOf( genre ) );
			}
			catch (Exception e) {
				// do nothing
			}
		}
		return result;
	}
}
