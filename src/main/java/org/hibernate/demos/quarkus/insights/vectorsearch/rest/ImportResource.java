package org.hibernate.demos.quarkus.insights.vectorsearch.rest;

import org.hibernate.demos.quarkus.insights.vectorsearch.service.ImportService;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path( "import" )
public class ImportResource {

	@Inject
	ImportService importService;

	@GET
	public String importBooks(){
		importService.importBooks();
		return "ok";
	}
}
