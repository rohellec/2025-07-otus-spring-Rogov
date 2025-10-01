package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JdbcBookRepository implements BookRepository {

    private final GenreRepository genreRepository;
    private final NamedParameterJdbcOperations namedJdbc;
    private final JdbcOperations jdbc;

    @Override
    public Optional<Book> findById(long id) {
        String bookQuery = """
                SELECT books.id, books.title, books.author_id, authors.full_name FROM books
                LEFT JOIN authors ON books.author_id = authors.id
                WHERE books.id = :id
                """;
        Book book = namedJdbc.query(bookQuery, Map.of("id", id),
                new BookResultSetExtractor());
        if (book == null) return Optional.empty();

        String relsQuery = "SELECT book_id, genre_id from books_genres WHERE book_id = :id";
        Set<Long> genreIds = namedJdbc.query(relsQuery, Map.of("id", id), new BookGenreRelationRowMapper())
                .stream().map(BookGenreRelation::genreId)
                .collect(Collectors.toSet());
        List<Genre> genres = genreRepository.findAllByIds(genreIds);
        book.setGenres(genres);
        return Optional.of(book);
    }

    @Override
    public List<Book> findAll() {
        var genres = genreRepository.findAll();
        var books = getAllBooksWithoutGenres();
        var relations = getAllGenreRelations();
        mergeBooksInfo(books, genres, relations);
        return books;
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            return insert(book);
        }
        return update(book);
    }

    @Override
    public void deleteById(long id) {
        String sqlQuery = "DELETE FROM books WHERE id = :id";
        namedJdbc.update(sqlQuery, Map.of("id", id));
    }

    private List<Book> getAllBooksWithoutGenres() {
        String sqlQuery = """
                SELECT books.id, books.title, books.author_id, authors.full_name FROM books
                LEFT JOIN authors ON books.author_id = authors.id
                """;
        return jdbc.query(sqlQuery, new BookRowMapper());
    }

    private List<BookGenreRelation> getAllGenreRelations() {
        String sqlQuery = "SELECT book_id, genre_id FROM books_genres";
        return jdbc.query(sqlQuery, new BookGenreRelationRowMapper());
    }

    private void mergeBooksInfo(List<Book> booksWithoutGenres, List<Genre> genres,
                                List<BookGenreRelation> relations) {
        for (Book book : booksWithoutGenres) {
            relations.stream()
                    .filter(rel -> book.getId() == rel.bookId)
                    .flatMap(rel ->
                            genres.stream()
                                    .filter(genre -> genre.getId() == rel.genreId))
                    .forEach(book::addGenre);
        }
    }

    private Book insert(Book book) {
        if (book.getAuthor() == null) {
            throw new EntityNotFoundException("Author is not specified");
        }
        var keyHolder = new GeneratedKeyHolder();
        String sqlQuery = "INSERT INTO books (title, author_id) VALUES (:title, :authorId)";
        long authorId = book.getAuthor().getId();
        SqlParameterSource params = new MapSqlParameterSource("title", book.getTitle())
                .addValue("authorId", authorId);
        namedJdbc.update(sqlQuery, params, keyHolder);

        //noinspection DataFlowIssue
        book.setId(keyHolder.getKey().longValue());
        batchInsertGenresRelationsFor(book);
        return book;
    }

    private Book update(Book book) {
        if (book.getAuthor() == null) {
            throw new EntityNotFoundException("Author is not specified");
        }
        String sqlQuery = """
                UPDATE books b SET
                b.title = :title,
                b.author_id = :authorId
                WHERE b.id = :id
                """;
        long authorId = book.getAuthor().getId();
        int updatedRows = namedJdbc.update(sqlQuery,
                Map.of( "id", book.getId(), "title", book.getTitle(), "authorId", authorId));
        if (updatedRows == 0) {
            throw new EntityNotFoundException("Book with id " + book.getId() + " not found");
        }
        removeGenresRelationsFor(book);
        batchInsertGenresRelationsFor(book);

        return book;
    }

    private void batchInsertGenresRelationsFor(Book book) {
        List<BookGenreRelation> relations = book.getGenres().stream()
                .map(genre -> new BookGenreRelation(book.getId(), genre.getId()))
                .toList();
        String sqlQuery = "INSERT INTO books_genres(book_id, genre_id) VALUES (:bookId, :genreId)";
        SqlParameterSource[] params = relations.stream()
                .map(relation ->
                        new MapSqlParameterSource("bookId", relation.bookId)
                            .addValue("genreId", relation.genreId))
                .toArray(SqlParameterSource[]::new);
        namedJdbc.batchUpdate(sqlQuery, params);
    }

    private void removeGenresRelationsFor(Book book) {
        String sqlQuery = "DELETE FROM books_genres WHERE book_id = :bookId";
        namedJdbc.update(sqlQuery, Map.of("bookId", book.getId()));
    }

    private static Book mapBook(ResultSet rs) throws SQLException {
        Book book = new Book();
        book.setId(rs.getLong("id"));
        book.setTitle(rs.getString("title"));
        long authorId = rs.getLong("author_id");
        String authorName = rs.getString("full_name");
        if (authorId != 0) {
            Author author = new Author(authorId, authorName);
            book.setAuthor(author);
        }
        return book;
    }

    private static class BookRowMapper implements RowMapper<Book> {

        @Override
        public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
            return mapBook(rs);
        }
    }

    @SuppressWarnings("ClassCanBeRecord")
    @RequiredArgsConstructor
    private static class BookResultSetExtractor implements ResultSetExtractor<Book> {

        @Override
        public Book extractData(ResultSet rs) throws SQLException, DataAccessException {
            if (!rs.next()) return null;
            return mapBook(rs);
        }
    }

    private record BookGenreRelation(long bookId, long genreId) {
    }

    private static class BookGenreRelationRowMapper implements RowMapper<BookGenreRelation> {

        @Override
        public BookGenreRelation mapRow(ResultSet rs, int rowNum) throws SQLException {
            long bookId = rs.getLong("book_id");
            long genreId = rs.getLong("genre_id");
            return new BookGenreRelation(bookId, genreId);
        }
    }
}
