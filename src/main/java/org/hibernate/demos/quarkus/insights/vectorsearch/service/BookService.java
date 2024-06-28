package org.hibernate.demos.quarkus.insights.vectorsearch.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.hibernate.demos.quarkus.insights.vectorsearch.domain.Author;
import org.hibernate.demos.quarkus.insights.vectorsearch.domain.Book;
import org.hibernate.demos.quarkus.insights.vectorsearch.domain.Genre;
import org.hibernate.demos.quarkus.insights.vectorsearch.dto.BookDto;
import org.hibernate.demos.quarkus.insights.vectorsearch.dto.Result;
import org.hibernate.demos.quarkus.insights.vectorsearch.search.TextEmbeddingModelBridge;
import org.hibernate.search.engine.search.query.SearchResult;
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
	TextEmbeddingModelBridge textEmbeddingModelBridge;

	@Inject
	ApplicationConfiguration configuration;

	@RestClient
	ExternalModelEmbeddingService embeddingService;

	@PersistenceContext
	EntityManager entityManager;

	@Inject
	SearchSession session;

	@Transactional
	public Book save(BookDto book) {
		Book bookEntity;
		if ( book.getId() != null ) {
			bookEntity = entityManager.getReference( Book.class, book.getId() );
		}
		else {
			bookEntity = new Book();
			entityManager.persist( bookEntity );
			book.setId( book.getId() );
		}

		Author author = entityManager.getReference( Author.class, book.getAuthor().getId() );
		bookEntity.author = author;
		String title = book.getTitle();
		bookEntity.title = title;
		Set<Genre> genres = book.getGenres();
		bookEntity.genres = genres;
		String summary = book.getSummary();
		bookEntity.summary = summary;

		return bookEntity;
	}

	@Transactional
	public void addCoverImage(Long id, byte[] coverImage) {
		Book bookEntity = entityManager.getReference( Book.class, id );

		addCoverImage( bookEntity, coverImage );
	}

	@Transactional
	public void addCoverImage(Book bookEntity, byte[] coverImage) {
		Path path = saveImage( coverImage );
		bookEntity.coverLocation = path.getFileName();
		bookEntity.coverEmbedding = embeddingService.imageEmbedding( path ).get( 0 );
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

	public Result<BookDto> similarBooks(Long id, int page) {
		Book book = entityManager.find( Book.class, id );
		int total = 20;
		SearchResult<BookDto> fetched = session.search( Book.class )
				.select( BookDto.class )
				.where( f -> f.bool()
						.must( f.terms().field( "genres" ).matchingAny( book.genres ) )
						.should( f.knn( total ).field( "coverEmbedding" ).matching( book.coverEmbedding )
								.requiredMinimumSimilarity( 0.60f )
								.filter( f.not( f.id().matching( id ) ) )
						)
						.should( f.knn( total ).field( "summary_embedding" )
								.matching( textEmbeddingModelBridge.toEmbedding( book.summary ) )
								.requiredMinimumSimilarity( 0.70f )
								.filter( f.not( f.id().matching( id ) ) )
						)
						.minimumShouldMatchNumber( 1 )
				)
				.highlighter( f -> f.fastVector().numberOfFragments( 1 ).fragmentSize( 10_000 ).noMatchSize( 10_000 ) )
				.fetch( page * 10, 10 );
		return new Result<>( fetched.total().hitCountLowerBound(), fetched.hits() );
	}

	public Result<BookDto> findBooks(String q, List<Genre> genres, int page) {
		SearchResult<BookDto> fetched = session.search( Book.class )
				.select( BookDto.class )
				.where( (f, root) -> {
					root.add( f.matchAll() );

					if ( !genres.isEmpty() ) {
						root.add( f.terms().field( "genres" ).matchingAny( genres ) );
					}
					if ( q != null && !q.isEmpty() ) {
						root.add( f.match().field( "title" )
								.field( "summary" )
								.field( "author.name" )
								.matching( q ) );
					}

				} )
				.highlighter( f -> f.fastVector()
						.tag( "<span class=highlight>", "</span>" )
						.numberOfFragments( 1 )
						.fragmentSize( 10_000 )
						.noMatchSize( 10_000 )
				).fetch( page * 10, 10 );
		return new Result<>( fetched.total().hitCountLowerBound(), fetched.hits() );
	}
}
