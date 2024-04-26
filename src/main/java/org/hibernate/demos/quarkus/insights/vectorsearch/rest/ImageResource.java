package org.hibernate.demos.quarkus.insights.vectorsearch.rest;

import org.hibernate.demos.quarkus.insights.vectorsearch.service.ApplicationConfiguration;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;

@Path("/image")
public class ImageResource {

	@Inject
	ApplicationConfiguration configuration;

	@GET
	@Path("/{name}")
	@Produces("image/png")
	public java.nio.file.Path getImage(@PathParam("name") String name) {
		return configuration.imagesRootPath().resolve( name );
	}
}
