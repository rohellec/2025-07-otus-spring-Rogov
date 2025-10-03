package ru.otus.hw.repositories;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class JdbcGenreRepository implements GenreRepository {

    private final NamedParameterJdbcOperations namedJdbc;
    private final JdbcOperations jdbc;

    public JdbcGenreRepository(NamedParameterJdbcOperations namedJdbc) {
        this.namedJdbc = namedJdbc;
        this.jdbc = namedJdbc.getJdbcOperations();
    }

    @Override
    public Optional<Genre> findById(long id) {
        String sqlQuery = "SELECT id, name FROM genres WHERE id = :id";
        Genre genre = namedJdbc.query(sqlQuery, Map.of("id", id), new GenreResultSetExtractor());
        return Optional.ofNullable(genre);
    }

    @Override
    public List<Genre> findAll() {
        return jdbc.query("SELECT id, name FROM genres", new GenreRowMapper());
    }

    @Override
    public List<Genre> findAllByIds(Set<Long> ids) {
        String sqlQuery = "SELECT id, name FROM genres WHERE id IN (:ids)";
        return namedJdbc.query(sqlQuery, Map.of("ids", ids),  new GenreRowMapper());
    }

    @Override
    public Genre save(Genre genre) {
        if (genre.getId() == 0) {
            return insert(genre);
        }
        return update(genre);
    }

    public void deleteById(long id) {
        String sqlQuery = "DELETE FROM genres WHERE id = :id";
        namedJdbc.update(sqlQuery, Map.of("id", id));
    }

    private Genre insert(Genre genre) {
        KeyHolder kh = new GeneratedKeyHolder();
        String sqlQuery = "INSERT INTO genres (name) VALUES (:name)";
        SqlParameterSource params = new MapSqlParameterSource("name", genre.getName());
        namedJdbc.update(sqlQuery, params, kh);

        //noinspection DataFlowIssue
        genre.setId(kh.getKey().longValue());
        return genre;
    }

    private Genre update(Genre genre) {
        String sqlQuery = "UPDATE genres SET name = :name WHERE id = :id";
        int updatedRows = namedJdbc.update(sqlQuery, Map.of("name", genre.getName(),  "id", genre.getId()));
        if (updatedRows == 0) {
            throw new EntityNotFoundException("Genre with id " + genre.getId() + " not found");
        }
        return genre;
    }

    private static Genre mapGenre(ResultSet rs) throws SQLException {
        Genre genre = new Genre();
        genre.setId(rs.getLong("id"));
        genre.setName(rs.getString("name"));
        return genre;
    }

    private static class GenreResultSetExtractor implements ResultSetExtractor<Genre> {

        @Override
        public Genre extractData(ResultSet rs) throws SQLException, DataAccessException {
            if (!rs.next()) return null;
            return mapGenre(rs);
        }
    }

    private static class GenreRowMapper implements RowMapper<Genre> {

        @Override
        public Genre mapRow(ResultSet rs, int i) throws SQLException {
            return  mapGenre(rs);
        }
    }
}
