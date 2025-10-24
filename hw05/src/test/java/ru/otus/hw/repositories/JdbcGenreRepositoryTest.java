package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Репозиторий на основе Jdbc для работы с жанрами ")
@JdbcTest
@Import({JdbcGenreRepository.class, JdbcBookRepository.class})
public class JdbcGenreRepositoryTest {

    @Autowired
    private JdbcGenreRepository jdbcGenreRepository;

    @Autowired
    private JdbcBookRepository jdbcBookRepository;

    List<Genre> dbGenres;

    @BeforeEach
    void setUp() {
        dbGenres = getDbGenres();
    }

    @DisplayName("должен загружать жанр по id")
    @ParameterizedTest
    @MethodSource("getDbGenres")
    public void shouldFindCorrespondingGenreById(Genre expected) {
        var actual = jdbcGenreRepository.findById(expected.getId());
        assertThat(actual).isPresent()
                .get()
                .isEqualTo(expected);
    }

    @DisplayName("должен загружать все жанры")
    @Test
    public void shouldFindAllGenres() {
        var expected = dbGenres;
        var actual = jdbcGenreRepository.findAll();
        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @DisplayName("должен загружать соответствующие жанры по их id")
    @Test
    public void shouldFindCorrespondingGenresByIds() {
        int lastExpectedIndex = new Random().nextInt(2, dbGenres.size() + 1);
        var expected = dbGenres.subList(0, lastExpectedIndex);
        var expectedIds = expected.stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());
        var actual = jdbcGenreRepository.findAllByIds(expectedIds);
        assertThat(actual).containsExactlyElementsOf(expected);
    }

    @DisplayName("должен сохранять новый жанр")
    @Test
    public void shouldSaveNewGenre() {
        var expected = new Genre(0, "Genre_100500");
        var saved = jdbcGenreRepository.save(expected);

        assertThat(saved).isNotNull()
                .matches(genre -> genre.getId() > 0)
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expected);

        var actual = jdbcGenreRepository.findById(saved.getId());
        assertThat(actual).isPresent()
                .get()
                .isEqualTo(saved);
    }

    @DisplayName("должен обновить существующий жанр")
    @Test
    public void shouldSaveUpdatedGenre() {
        var expected = new Genre(1L, "GenreName_100500");
        var actual = jdbcGenreRepository.findById(expected.getId());

        assertThat(actual).isPresent()
                .get()
                .isNotEqualTo(expected);

        var saved = jdbcGenreRepository.save(expected);
        assertThat(saved).isNotNull()
                .matches(genre -> genre.getId() == 1L)
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expected);

        actual = jdbcGenreRepository.findById(saved.getId());
        assertThat(actual).isPresent()
                .get()
                .isEqualTo(saved);
    }

    @DisplayName("должен кинуть исключение в случае отсутствия записи")
    @Test
    public void shouldThrowExceptionWhenUpdatedGenreIsNotPresent() {
        var expected = new Genre(1000L, "GenreName_100500");
        var actual = jdbcGenreRepository.findById(expected.getId());

        assertThat(actual).isEmpty();

        assertThatThrownBy(() -> jdbcGenreRepository.save(expected))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("not found");
    }

    @DisplayName("должен удалить существующий жанр по id")
    @Test
    public void shouldDeleteGenreById() {
        assertThat(jdbcGenreRepository.findById(1L)).isPresent();
        jdbcGenreRepository.deleteById(1L);
        assertThat(jdbcGenreRepository.findById(1L)).isEmpty();

        jdbcBookRepository.findAll().forEach(book -> {
            var genreIds = book.getGenres().stream().map(Genre::getId).collect(Collectors.toSet());
            assertThat(genreIds).doesNotContain(1L);
        });
    }

    private static List<Genre> getDbGenres() {
        return IntStream.range(1, 7).boxed()
                .map(i -> new Genre(i, "Genre_" + i))
                .toList();
    }
}
