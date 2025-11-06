package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.converters.AuthorConverter;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.converters.GenreConverter;
import ru.otus.hw.repositories.*;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.BookServiceImpl;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.CommentServiceImpl;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

@DisplayName("Сервис для работы с книгами")
@DataJpaTest
@Transactional(propagation = Propagation.NEVER)
@Import({BookServiceImpl.class, BookConverter.class, JpaBookRepository.class,
        JpaAuthorRepository.class, AuthorConverter.class,
        JpaGenreRepository.class, GenreConverter.class,
        CommentServiceImpl.class, CommentConverter.class, JpaCommentRepository.class})
public class BookServiceTest {

    private static final long FIRST_BOOK_ID = 1L;
    private static final long SECOND_BOOK_ID = 2L;
    private static final long FIRST_AUTHOR_ID = 1L;

    @Autowired
    private BookService bookService;

    @Autowired
    private CommentService commentService;

    private Set<Long> dbGenreIds;

    @BeforeEach
    void setUp() {
        dbGenreIds = getGenreIds();
    }

    @DisplayName("не должен бросать LazyInitializationException при запросе книги по id")
    @ParameterizedTest
    @MethodSource("getBookIds")
    void shouldNotThrowExceptionWhileGettingById(long id) {
        assertThatNoException().isThrownBy(() -> bookService.findById(id));
    }

    @DisplayName("не должен бросать LazyInitializationException при запросе всех книг")
    @Test
    void shouldNotThrowExceptionWhileGettingAllBooks() {
        assertThatNoException().isThrownBy(() -> bookService.findAll());
    }

    @DisplayName("не должен бросать LazyInitializationException при создании книги")
    @Test
    void shouldNotThrowExceptionWhileCreatingBook() {
        assertThatNoException().isThrownBy(
                () -> bookService.insert("NewBook_99", FIRST_AUTHOR_ID, dbGenreIds));

    }

    @DisplayName("не должен бросать LazyInitializationException при обновлении книги")
    @Test
    void shouldNotThrowExceptionWhileUpdatingBook() {
        assertThatNoException().isThrownBy(
                () -> bookService.update(FIRST_BOOK_ID,"UpdatedTitle_1111", FIRST_AUTHOR_ID, dbGenreIds));

    }

    @Transactional
    @DisplayName("не должен бросать LazyInitializationException при удалении книги по id")
    @ParameterizedTest
    @MethodSource("getBookIds")
    void shouldNotThrowExceptionWhileDeletingById(long id) {
        assertThatNoException().isThrownBy(() -> bookService.deleteById(id));
    }

    @DisplayName("при удалении книги так же должен удалять и комментарии")
    @Test
    void shouldDeleteCommentsAfterDeletingBook() {
        bookService.deleteById(SECOND_BOOK_ID);
        assertThat(commentService.findAllByBookId(SECOND_BOOK_ID)).isEmpty();
    }

    private static long[] getBookIds() {
        return LongStream.range(1, 4).toArray();
    }

    private static Set<Long> getGenreIds() {
        return LongStream.range(1, 7).collect(
                HashSet::new,
                HashSet<Long>::add,
                HashSet<Long>::addAll
        );
    }
}
