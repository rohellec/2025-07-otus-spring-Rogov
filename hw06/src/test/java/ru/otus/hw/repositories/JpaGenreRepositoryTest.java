package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Репозиторий на основе JPA для работы с жанрами ")
@DataJpaTest
@Import(JpaGenreRepository.class)
public class JpaGenreRepositoryTest {

    private static final long FIRST_GENRE_ID = 1L;

    @Autowired
    private TestEntityManager em;

    @Autowired
    private JpaGenreRepository jpaGenreRepository;

    List<Genre> dbGenres;

    @BeforeEach
    void setUp() {
        dbGenres = getDbGenres();
    }

    @DisplayName("должен загружать жанр по id")
    @ParameterizedTest
    @MethodSource("getDbGenres")
    public void shouldFindCorrespondingGenreById(Genre genre) {
        var expected = em.find(Genre.class, genre.getId());
        var actual = jpaGenreRepository.findById(expected.getId());
        assertThat(actual).isPresent()
                .get()
                .usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("должен загружать все жанры")
    @Test
    public void shouldFindAllGenres() {
        var expected = dbGenres;
        var actual = jpaGenreRepository.findAll();
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
        var actual = jpaGenreRepository.findAllByIds(expectedIds);
        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @DisplayName("должен сохранять новый жанр")
    @Test
    public void shouldSaveNewGenre() {
        var expected = new Genre(0, "Genre_100500");
        var saved = jpaGenreRepository.save(expected);

        assertThat(saved).isNotNull()
                .matches(genre -> genre.getId() > 0)
                .usingRecursiveComparison().ignoringExpectedNullFields()
                .isEqualTo(expected);

        var actual = em.find(Genre.class, saved.getId());
        assertThat(actual)
                .isNotNull()
                .isEqualTo(saved);
    }

    @DisplayName("должен обновить существующий жанр")
    @Test
    public void shouldSaveUpdatedGenre() {
        var expected = new Genre(FIRST_GENRE_ID, "GenreName_100500");
        var actual = em.find(Genre.class, expected.getId());

        assertThat(actual)
                .isNotNull()
                .isNotEqualTo(expected);

        var saved = jpaGenreRepository.save(expected);
        assertThat(saved).isNotNull()
                .matches(genre -> genre.getId() == expected.getId())
                .usingRecursiveComparison().ignoringExpectedNullFields()
                .isEqualTo(expected);

        actual = em.find(Genre.class, saved.getId());
        assertThat(actual)
                .isNotNull()
                .isEqualTo(saved);
    }

    @DisplayName("должен кинуть исключение в случае обновления отсутствующего жанра")
    @Test
    public void shouldThrowExceptionWhenUpdatedGenreIsNotPresent() {
        var expected = new Genre(1000L, "GenreName_100500");
        var actual = em.find(Genre.class, expected.getId());
        assertThat(actual).isNull();

        assertThatThrownBy(() -> jpaGenreRepository.save(expected))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("not found");
    }

    @DisplayName("должен удалить существующий жанр по id")
    @Test
    public void shouldDeleteGenreById() {
        assertThat(em.find(Genre.class, FIRST_GENRE_ID)).isNotNull();
        jpaGenreRepository.deleteById(FIRST_GENRE_ID);
        assertThat(em.find(Genre.class, FIRST_GENRE_ID)).isNull();
    }

    private static List<Genre> getDbGenres() {
        return IntStream.range(1, 7).boxed()
                .map(i -> new Genre(i, "Genre_" + i))
                .toList();
    }
}
