package org.hibernate.demos.quarkus.insights.vectorsearch.service;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.hibernate.demos.quarkus.insights.vectorsearch.domain.Author;
import org.hibernate.demos.quarkus.insights.vectorsearch.domain.Book;
import org.hibernate.demos.quarkus.insights.vectorsearch.domain.Genre;
import org.hibernate.demos.quarkus.insights.vectorsearch.dto.AuthorDto;
import org.hibernate.demos.quarkus.insights.vectorsearch.dto.BookDto;

import io.quarkus.narayana.jta.QuarkusTransaction;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

@ApplicationScoped
public class ImportService {
	@PersistenceContext
	EntityManager entityManager;
	@Inject
	BookService bookService;

	public void importBooks() {
		try {
			List<String> books = Files.readAllLines( Path.of( "data/books.txt" ) );
			for ( String book : books ) {
				System.err.println( "importing book: " + book );
				try {
					importBook( book );
				}
				catch (IOException e) {
					// do nothing
					System.err.println( e.getMessage() );
				}
			}
		}
		catch (IOException e) {
			throw new RuntimeException( e );
		}
	}

	@Transactional
	public void importBook(String link) throws IOException {
		Document doc = Jsoup.connect( link ).get();
		String title = doc.body().select( ".BookPageTitleSection__title h1" ).text();
		String summary = doc.body().select( ".BookPageMetadataSection__description" ).text();
		List<String> genres = doc.body().select( ".BookPageMetadataSection__genreButton" )
				.stream().map( Element::text )
				.toList();

		String author = doc.select( ".BookPageMetadataSection__contributor" ).text();

		Elements image = doc.select( ".BookCover__image img" );
		String src = image.first().attr( "src" );


		BookDto book = new BookDto( title, summary, Genre.convert( genres ), getAuthor( author ) );

		Book saved = bookService.save( book );
		try ( BufferedInputStream in = new BufferedInputStream( new URL( src ).openStream() ) ) {
			byte[] cover = in.readAllBytes();
			bookService.addCoverImage( saved, cover );
		}
	}

	private AuthorDto getAuthor(String name) {
		List<Author> authors = entityManager.createQuery( "select a from Author a where a.name = :name", Author.class )
				.setParameter( "name", name )
				.getResultList();

		AtomicLong authorId = new AtomicLong();
		if ( authors.isEmpty() ) {
			QuarkusTransaction.requiringNew().run( () -> {
				Author a = new Author();
				a.name = name;
				entityManager.persist( a );
				authorId.set( a.id );
			} );
		}
		else {
			authorId.set( authors.get( 0 ).id );
		}
		AuthorDto dto = new AuthorDto( authorId.get(), null );

		return dto;
	}

}
