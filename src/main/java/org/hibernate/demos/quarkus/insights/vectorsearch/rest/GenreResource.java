package org.hibernate.demos.quarkus.insights.vectorsearch.rest;

import org.hibernate.demos.quarkus.insights.vectorsearch.domain.Genre;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/genres")
public class GenreResource {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Genre[] genres() {
		return Genre.values();
	}

}
