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
import ru.otus.hw.models.Author;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Репозиторий на основе JPA для работы с авторами ")
@DataJpaTest
@Import(JpaAuthorRepository.class)
public class JpaAuthorRepositoryTest {

    private static final long FIRST_AUTHOR_ID = 1L;

    @Autowired
    private TestEntityManager em;

    @Autowired
    private JpaAuthorRepository jpaAuthorRepository;

    private List<Author> dbAuthors;

    @BeforeEach
    void setUp() {
        dbAuthors = getDbAuthors();
    }

    @DisplayName("должен загружать автора по id")
    @ParameterizedTest
    @MethodSource("getDbAuthors")
    void shouldFindCorrespondingAuthorById(Author author) {
        var expected = em.find(Author.class, author.getId());
        var actual = jpaAuthorRepository.findById(expected.getId());
        assertThat(actual).isPresent()
                .get()
                .usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("должен загружать всех авторов")
    @Test
    void shouldFindAllAuthors() {
        var expected = dbAuthors;
        var actual = jpaAuthorRepository.findAll();
        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @DisplayName("должен сохранять нового автора")
    @Test
    void shouldSaveNewAuthor() {
        var expected = new Author(0, "Author_100500");
        var saved = jpaAuthorRepository.save(expected);

        assertThat(saved).isNotNull()
                .matches(author -> author.getId() > 0)
                .usingRecursiveComparison().ignoringExpectedNullFields()
                .isEqualTo(expected);

        var actual = em.find(Author.class, saved.getId());
        assertThat(actual)
                .isNotNull()
                .isEqualTo(saved);
    }

    @DisplayName("должен обновлять существующего автора")
    @Test
    void shouldSaveUpdatedAuthor() {
        var expected = new Author(FIRST_AUTHOR_ID, "Author_100500");
        var actual = em.find(Author.class, expected.getId());

        assertThat(actual)
                .isNotNull()
                .isNotEqualTo(expected);

        var saved = jpaAuthorRepository.save(expected);
        assertThat(saved).isNotNull()
                .matches(author -> author.getId() == expected.getId())
                .usingRecursiveComparison().ignoringExpectedNullFields()
                .isEqualTo(expected);

        actual = em.find(Author.class, expected.getId());
        assertThat(actual)
                .isNotNull()
                .isEqualTo(saved);
    }

    @DisplayName("должен кидать исключение при обновлении несуществующего автора")
    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingAuthor() {
        var expected = new Author(100500L, "Author_100500");
        var actual = em.find(Author.class, expected.getId());

        assertThat(actual).isNull();
        assertThatThrownBy(() -> jpaAuthorRepository.save(expected))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Author with id " + expected.getId() + " not found");
    }

    @DisplayName("должен удалять автора по id")
    @Test
    void shouldDeleteAuthorById() {
        assertThat(em.find(Author.class,FIRST_AUTHOR_ID)).isNotNull();
        jpaAuthorRepository.deleteById(1L);
        assertThat(em.find(Author.class,FIRST_AUTHOR_ID)).isNull();
    }

    private static List<Author> getDbAuthors() {
        return IntStream.range(1, 4).boxed()
                .map(id -> new Author(id, "Author_" + id))
                .toList();
    }
}
