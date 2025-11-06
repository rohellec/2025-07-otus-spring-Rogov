package ru.otus.hw.service;

import org.junit.jupiter.api.DisplayName;
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
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.JpaAuthorRepository;
import ru.otus.hw.repositories.JpaBookRepository;
import ru.otus.hw.repositories.JpaCommentRepository;
import ru.otus.hw.repositories.JpaGenreRepository;
import ru.otus.hw.services.*;

import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Сервис для работы с авторами ")
@DataJpaTest
@Transactional(propagation = Propagation.NEVER)
@Import({AuthorServiceImpl.class, JpaAuthorRepository.class, AuthorConverter.class,
        BookServiceImpl.class, BookConverter.class, JpaBookRepository.class,
        JpaGenreRepository.class, GenreConverter.class,
        CommentServiceImpl.class, CommentConverter.class, JpaCommentRepository.class})
public class AuthorServiceTest {

    @Autowired
    private AuthorService authorService;

    @Autowired
    private BookService bookService;

    @DisplayName("при удалении автора так же должен удалять книги автора")
    @ParameterizedTest
    @MethodSource("getAuthorIds")
    void shouldDeleteAuthorBooksAfterDeletingAuthor(long id) {
        authorService.deleteById(id);
        var authorIds = bookService.findAll().stream()
                .map(book -> book.getAuthorDto().getId()).toList();
        assertThat(authorIds).doesNotContain(id);
    }

    private static long[] getAuthorIds() {
        return LongStream.range(1, 4).toArray();
    }
}
