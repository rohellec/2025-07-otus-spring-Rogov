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
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Репозиторий на основе JPA для работы с комментариями ")
@DataJpaTest
@Import(JpaCommentRepository.class)
public class JpaCommentRepositoryTest {

    private static final long FIRST_COMMENT_ID = 1L;
    private static final long FIRST_BOOK_ID = 1L;

    @Autowired
    TestEntityManager em;

    @Autowired
    JpaCommentRepository commentRepository;

    private List<Book> dbBooks;

    private List<Comment> dbComments;

    @BeforeEach
    public void setUp() {
        dbBooks = getDbBooks();
        dbComments = getDbComments();
    }

    @DisplayName("должен загружать комментарий по id")
    @ParameterizedTest
    @MethodSource("getDbComments")
    void shouldReturnCorrectCommentById(Comment comment) {
        var expected = em.find(Comment.class, comment.getId());
        var actual = commentRepository.findById(expected.getId());
        assertThat(actual).isPresent()
                .get()
                .usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("должен загружать список комментариев для книги")
    @Test
    void shouldReturnCorrectBookComments() {
        var expected = dbComments.stream()
                .filter(c -> c.getBook().getId() == FIRST_BOOK_ID)
                .toList();
        var actual = commentRepository.findAllByBookId(FIRST_BOOK_ID);

        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
        actual.forEach(System.out::println);
    }

    @Test
    @DisplayName("должен сохранять новый комментарий")
    public void shouldInsertNewComment() {
        Book book = em.find(Book.class, FIRST_BOOK_ID);
        var expected = new Comment(0, "Comment_100", book);
        var returned = commentRepository.save(expected);
        assertThat(returned).isNotNull()
                .matches(comment -> comment.getId() > 0)
                .usingRecursiveComparison().isEqualTo(expected);

        var actual = em.find(Comment.class, returned.getId());
        assertThat(actual).isNotNull()
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("должен бросать исключение при сохранении нового комментария без книги")
    public void shouldThrowExceptionWhenSavingNewCommentWithoutBook() {
        Comment comment = new Comment(0, "Comment_100", null);
        assertThatThrownBy(() -> commentRepository.save(comment))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("not specified");
    }

    @Test
    @DisplayName("должен обновлять существующий комментарий")
    public void shouldUpdateExistingComment() {
        var expected = new Comment(FIRST_COMMENT_ID, "Comment_100500", dbBooks.get(2));
        assertThat(em.find(Comment.class, expected.getId()))
                .isNotNull()
                .isNotEqualTo(expected);

        var returned = commentRepository.save(expected);
        assertThat(returned).isNotNull()
                .matches(comment -> comment.getId() == expected.getId())
                .isEqualTo(expected);

        var actual = em.find(Comment.class, returned.getId());
        assertThat(actual).isNotNull()
                .isEqualTo(expected);
    }

    @DisplayName("должен бросать исключение при сохранении несуществующего обновлённого комментария")
    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingComment() {
        var expected = new Comment(100500L, "Comment_100500", dbBooks.get(0));
        assertThat(em.find(Comment.class, expected.getId()))
                .isNull();

        assertThatThrownBy(() -> commentRepository.save(expected))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("not found");
    }

    @Test
    @DisplayName("должен бросать исключение при сохранении обновлённого комментария без книги")
    public void shouldThrowExceptionWhenSavingUpdatedCommentWithoutBook() {
        var expected = new Comment(FIRST_COMMENT_ID, "Comment_100500", null);
        assertThat(em.find(Comment.class, expected.getId()))
                .isNotNull()
                .isNotEqualTo(expected);

        assertThatThrownBy(() -> commentRepository.save(expected))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("not specified");
    }

    @Test
    @DisplayName("должен удалять существующий комментарий по id")
    public void shouldDeleteComment() {
        assertThat(em.find(Comment.class, FIRST_COMMENT_ID)).isNotNull();
        commentRepository.deleteById(FIRST_COMMENT_ID);
        assertThat(em.find(Comment.class, FIRST_COMMENT_ID)).isNull();
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
                        dbGenres.subList(id * 2 - 2, id * 2)
                ))
                .toList();
    }

    private static List<Book> getDbBooks() {
        var dbAuthors = getDbAuthors();
        var dbGenres = getDbGenres();
        return getDbBooks(dbAuthors, dbGenres);
    }

    private static List<Comment> getDbComments() {
        var dbBooks = getDbBooks();
        return IntStream.range(1, 7).boxed()
                .map(id -> new Comment(id,
                        "Comment_" + id,
                        dbBooks.get((id - 1) / 2)))
                .toList();
    }
}
