package ru.otus.hw.repositories;

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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class JdbcAuthorRepository implements AuthorRepository {

    private final NamedParameterJdbcOperations namedJdbc;

    private final JdbcOperations jdbc;

    public JdbcAuthorRepository(NamedParameterJdbcOperations namedJdbc) {
        this.namedJdbc = namedJdbc;
        this.jdbc = namedJdbc.getJdbcOperations();
    }

    @Override
    public List<Author> findAll() {
        String sqlQuery = "SELECT id, full_name FROM authors";
        return jdbc.query(sqlQuery, new AuthorRowMapper());
    }

    @Override
    public Optional<Author> findById(long id) {
        String sqlQuery = "SELECT id, full_name FROM authors WHERE id = :id";
        Author author = namedJdbc.query(sqlQuery, Map.of("id", id), new AuthorResultSetExtractor());
        return Optional.ofNullable(author);
    }

    @Override
    public Author save(Author author) {
        if (author.getId() == 0) {
            return insert(author);
        }
        return update(author);
    }

    @Override
    public void deleteById(long id) {
        String sqlQuery = "DELETE FROM authors WHERE id = :id";
        namedJdbc.update(sqlQuery, Map.of("id", id));
    }

    private Author insert(Author author) {
        var keyHolder = new GeneratedKeyHolder();
        String sqlQuery = "INSERT INTO authors (full_name) VALUES (:name)";
        SqlParameterSource params = new MapSqlParameterSource("name", author.getFullName());
        namedJdbc.update(sqlQuery, params, keyHolder);

        //noinspection DataFlowIssue
        author.setId(keyHolder.getKey().longValue());
        return author;
    }

    private Author update(Author author) {
        String sqlQuery = """
                UPDATE authors SET full_name = :name
                WHERE id = :id
                """;
        int updatedRows = namedJdbc.update(sqlQuery,
                Map.of("id", author.getId(), "name", author.getFullName()));
        if (updatedRows == 0)
            throw new EntityNotFoundException("Author with id " + author.getId() + " not found");
        return author;
    }

    private static Author mapAuthor(ResultSet rs) throws SQLException {
        Author author = new Author();
        author.setId(rs.getLong("id"));
        author.setFullName(rs.getString("full_name"));
        return author;
    }

    private static class AuthorResultSetExtractor implements ResultSetExtractor<Author> {

        @Override
        public Author extractData(ResultSet rs) throws SQLException {
            if (!rs.next()) return null;
            return mapAuthor(rs);
        }
    }

    private static class AuthorRowMapper implements RowMapper<Author> {

        @Override
        public Author mapRow(ResultSet rs, int i) throws SQLException {
            return mapAuthor(rs);
        }
    }
}
