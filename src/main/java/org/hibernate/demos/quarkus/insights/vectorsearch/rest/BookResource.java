package org.hibernate.demos.quarkus.insights.vectorsearch.rest;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.hibernate.demos.quarkus.insights.vectorsearch.domain.Genre;
import org.hibernate.demos.quarkus.insights.vectorsearch.dto.BookDto;
import org.hibernate.demos.quarkus.insights.vectorsearch.service.BookService;

import org.jboss.resteasy.reactive.RestQuery;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/books")
public class BookResource {

	private final BookService service;

	public BookResource(BookService service) {
		this.service = service;
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public BookDto save(BookDto book) {
		service.save( book );
		return book;
	}

	@PATCH
	@Path("/add-cover/{id}")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	public String addCoverImage(Long id, InputStream cover) throws IOException {
		service.addCoverImage( id, cover.readAllBytes() );
		return "Image added";
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<BookDto> findBooks(
			@RestQuery String q,
			@RestQuery List<Genre> genres,
			@RestQuery @DefaultValue("0") int page
	) {
		return service.findBooks( q, genres, page );
	}

	@GET
	@Path("/{id}/similar")
	@Produces(MediaType.APPLICATION_JSON)
	public List<BookDto> findBooks(
			Long id
	) {
		return service.similarBooks( id );
	}
}
