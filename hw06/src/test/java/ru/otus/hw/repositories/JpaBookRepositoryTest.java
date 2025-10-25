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
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Репозиторий на основе JPA для работы с книгами ")
@DataJpaTest
@Import(JpaBookRepository.class)
class JpaBookRepositoryTest {

    private static final long FIRST_BOOK_ID = 1L;
    private static final long FIRST_AUTHOR_ID = 1L;
    private static final long FIRST_GENRE_ID = 1L;

    @Autowired
    private TestEntityManager em;

    @Autowired
    private JpaBookRepository jpaRepository;

    private List<Author> dbAuthors;

    private List<Genre> dbGenres;

    private List<Book> dbBooks;

    @BeforeEach
    void setUp() {
        dbAuthors = getDbAuthors();
        dbGenres = getDbGenres();
        dbBooks = getDbBooks(dbAuthors, dbGenres);
    }

    @DisplayName("должен загружать книгу по id")
    @ParameterizedTest
    @MethodSource("getDbBooks")
    void shouldReturnCorrectBookById(Book book) {
        var expected = em.find(Book.class, book.getId());
        var actual = jpaRepository.findById(expected.getId());
        assertThat(actual).isPresent()
                .get()
                .usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("должен загружать список всех книг")
    @Test
    void shouldReturnCorrectBooksList() {
        var expected = dbBooks;
        var actual = jpaRepository.findAll();

        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
        actual.forEach(System.out::println);
    }

    @DisplayName("должен сохранять новую книгу")
    @Test
    void shouldSaveNewBook() {
        var author = em.find(Author.class, FIRST_AUTHOR_ID);
        var genre = em.find(Genre.class, FIRST_GENRE_ID);
        var expected = new Book(0, "BookTitle_10500", author, List.of(genre));
        var returned = jpaRepository.save(expected);
        assertThat(returned).isNotNull()
                .matches(book -> book.getId() > 0)
                .usingRecursiveComparison().ignoringExpectedNullFields()
                .isEqualTo(expected);

        assertThat(em.find(Book.class, returned.getId()))
                .isNotNull()
                .isEqualTo(returned);
    }

    @DisplayName("должен бросать исключение при сохранении новой книги без автора")
    @Test
    void shouldThrowExceptionWhenSavingNewBookWithoutAuthor() {
        var expected = new Book(0, "BookTitle_10500", null,
                List.of(dbGenres.get(0), dbGenres.get(2)));
        assertThatThrownBy(() -> jpaRepository.save(expected))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("not specified");
    }

    @DisplayName("должен сохранять измененную книгу")
    @Test
    void shouldSaveUpdatedBook() {
        var expected = new Book(FIRST_BOOK_ID, "BookTitle_10500", dbAuthors.get(2),
                List.of(dbGenres.get(4), dbGenres.get(5)));

        assertThat(em.find(Book.class, expected.getId()))
                .isNotNull()
                .isNotEqualTo(expected);

        var returned = jpaRepository.save(expected);
        assertThat(returned).isNotNull()
                .matches(book -> book.getId() == expected.getId())
                .usingRecursiveComparison().ignoringExpectedNullFields()
                .isEqualTo(expected);

        assertThat(em.find(Book.class, returned.getId()))
                .isNotNull()
                .isEqualTo(returned);
    }

    @DisplayName("должен бросать исключение при обновлении несуществующей книги")
    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingBook() {
        var expected = new Book(1000L, "BookTitle_1000", dbAuthors.get(0),
                List.of(dbGenres.get(1), dbGenres.get(2)));

        assertThat(em.find(Book.class, expected.getId()))
                .isNull();

        assertThatThrownBy(() -> jpaRepository.save(expected))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("not found");
    }

    @DisplayName("должен бросать исключение при сохранении обновлённой книги без автора")
    @Test
    void shouldThrowExceptionWhenSavingUpdatedBookWithoutAuthor() {
        var expected = new Book(FIRST_BOOK_ID, "BookTitle_1", null,
                List.of(dbGenres.get(1), dbGenres.get(2)));

        assertThat(em.find(Book.class, expected.getId()))
                .isNotNull()
                .isNotEqualTo(expected);

        assertThatThrownBy(() -> jpaRepository.save(expected))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("not specified");
    }

    @DisplayName("должен удалять книгу по id ")
    @Test
    void shouldDeleteBook() {
        assertThat(em.find(Book.class, FIRST_BOOK_ID)).isNotNull();
        jpaRepository.deleteById(FIRST_BOOK_ID);
        assertThat(em.find(Book.class, FIRST_BOOK_ID)).isNull();
    }

    private static List<Author> getDbAuthors() {
        return IntStream.range(1, 4).boxed()
                .map(id -> new Author(id, "Author_" + id))
                .toList();
    }

    private static List<Genre> getDbGenres() {
        return IntStream.range(1, 7).boxed()
                .map(id -> new Genre(id, "Genre_" + id))
                .toList();
    }

    private static List<Book> getDbBooks(List<Author> dbAuthors, List<Genre> dbGenres) {
        return IntStream.range(1, 4).boxed()
                .map(id -> new Book(id,
                        "BookTitle_" + id,
                        dbAuthors.get(id - 1),
                        dbGenres.subList((id - 1) * 2, (id - 1) * 2 + 2)
                ))
                .toList();
    }

    private static List<Book> getDbBooks() {
        var dbAuthors = getDbAuthors();
        var dbGenres = getDbGenres();
        return getDbBooks(dbAuthors, dbGenres);
    }
}