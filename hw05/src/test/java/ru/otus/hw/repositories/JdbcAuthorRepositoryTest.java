package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Jdbc для работы с авторами ")
@JdbcTest
@Import({JdbcAuthorRepository.class, JdbcBookRepository.class})
public class JdbcAuthorRepositoryTest {

    @Autowired
    private JdbcAuthorRepository jdbcAuthorRepository;

    @Autowired
    private JdbcBookRepository jdbcBookRepository;

    @MockitoBean
    private GenreRepository genreRepository;

    private List<Author> dbAuthors;

    @BeforeEach
    void setUp() {
        dbAuthors = getDbAuthors();
    }

    @DisplayName("должен загружать автора по id")
    @ParameterizedTest
    @MethodSource("getDbAuthors")
    void shouldFindCorrespondingAuthorById(Author expected) {
        var actual = jdbcAuthorRepository.findById(expected.getId());
        assertThat(actual).isPresent()
                .get()
                .isEqualTo(expected);
    }

    @DisplayName("должен загружать всех авторов")
    @Test
    void shouldFindAllAuthors() {
        var expected = dbAuthors;
        var actual = jdbcAuthorRepository.findAll();
        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @DisplayName("должен сохранять нового автора")
    @Test
    void shouldSaveNewAuthor() {
        var expected = new Author(0L, "Author_100500");
        var saved = jdbcAuthorRepository.save(expected);

        assertThat(saved).isNotNull()
                .matches(author -> author.getId() > 0)
                .usingRecursiveComparison().ignoringExpectedNullFields()
                .isEqualTo(expected);

        var actual = jdbcAuthorRepository.findById(saved.getId());
        assertThat(actual).isPresent()
                .get()
                .isEqualTo(saved);
    }

    @DisplayName("должен обновлять существующего автора")
    @Test
    void shouldSaveUpdatedAuthor() {
        var expected = new Author(1L, "Author_100500");
        var actual = jdbcAuthorRepository.findById(expected.getId());

        assertThat(actual).isPresent()
                .get()
                .isNotEqualTo(expected);

        var saved = jdbcAuthorRepository.save(expected);

        assertThat(saved).isNotNull()
                .matches(author -> author.getId() == 1L)
                .usingRecursiveComparison().ignoringExpectedNullFields()
                .isEqualTo(expected);

        actual = jdbcAuthorRepository.findById(saved.getId());
        assertThat(actual).isPresent()
                .get()
                .isEqualTo(saved);
    }

    @DisplayName("должен удалять автора по id")
    @Test
    void shouldDeleteAuthorById() {
        assertThat(jdbcAuthorRepository.findById(1L)).isPresent();
        jdbcAuthorRepository.deleteById(1L);
        assertThat(jdbcAuthorRepository.findById(1L)).isEmpty();

        jdbcBookRepository.findAll().forEach(book -> {
           assertThat(book.getAuthor().getId()).isNotEqualTo(1L);
        });
    }

    private static List<Author> getDbAuthors() {
        return IntStream.range(1, 4).boxed()
                .map(id -> new Author(id, "Author_" + id))
                .toList();
    }
}
