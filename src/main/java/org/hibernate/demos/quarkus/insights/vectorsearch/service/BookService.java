package org.hibernate.demos.quarkus.insights.vectorsearch.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import org.hibernate.demos.quarkus.insights.vectorsearch.domain.Author;
import org.hibernate.demos.quarkus.insights.vectorsearch.domain.Book;
import org.hibernate.demos.quarkus.insights.vectorsearch.domain.Genre;
import org.hibernate.demos.quarkus.insights.vectorsearch.dto.BookDto;
import org.hibernate.search.mapper.orm.session.SearchSession;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class BookService {

	@Inject
	ApplicationConfiguration configuration;

	@RestClient
	ExternalModelEmbeddingService embeddingService;

	@PersistenceContext
	EntityManager entityManager;

	@Inject
	SearchSession session;

	@Transactional
	public void save(BookDto book) {
		Book bookEntity;
		if ( book.getId() != null ) {
			bookEntity = entityManager.getReference( Book.class, book.getId() );
		}
		else {
			bookEntity = new Book();
			entityManager.persist( bookEntity );
			book.setId( book.getId() );
		}

		bookEntity.setAuthor( entityManager.getReference( Author.class, book.getAuthor().getId() ) );
		bookEntity.setTitle( book.getTitle() );
		bookEntity.setGenres( book.getGenres() );
		bookEntity.setSummary( book.getSummary() );
	}

	@Transactional
	public void addCoverImage(Long id, byte[] coverImage) {
		Book bookEntity = entityManager.getReference( Book.class, id );

		bookEntity.setCoverLocation( saveImage( coverImage ) );
		bookEntity.setCoverEmbedding( embeddingService.imageEmbedding( bookEntity.getCoverLocation() ).get( 0 ) );
	}


	private Path saveImage(byte[] coverImage) {
		Path path = configuration.imagesRootPath()
				.resolve( UUID.randomUUID().toString() );
		try ( FileOutputStream outputStream = new FileOutputStream( path.toFile() ) ) {
			outputStream.write( coverImage );
		}
		catch (IOException e) {
			throw new RuntimeException( e );
		}
		return path;
	}

	public List<BookDto> similarBooks(Long id) {
		Book book = entityManager.find( Book.class, id );
		return session.search( Book.class )
				.where( f -> f.bool()
						.should( f.terms().field( "genres" ).matchingAny( book.getGenres() ) )
						.should( f.knn( 15 ).field( "coverEmbedding" ).matching( book.getCoverEmbedding() ) )
				).fetchHits( 10 )
				.stream()
				.map( BookDto::new )
				.toList();
	}

	public List<BookDto> findBooks(String q, List<Genre> genres, int page) {
		return session.search( Book.class )
				.where( f -> f.bool()
						.must( f.terms().field( "genres" ).matchingAny( genres ) )
						.must( f.match().field( "title" )
								.field( "summary" )
								.field( "author" )
								.matching( q )
						)
				).fetchHits( page * 10, 10 )
				.stream()
				.map( BookDto::new )
				.toList();
	}
}
