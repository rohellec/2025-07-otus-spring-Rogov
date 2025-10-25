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
import ru.otus.hw.converters.GenreConverter;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.repositories.JpaAuthorRepository;
import ru.otus.hw.repositories.JpaBookRepository;
import ru.otus.hw.repositories.JpaGenreRepository;
import ru.otus.hw.services.*;

import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Сервис для работы с жанрами ")
@DataJpaTest
@Transactional(propagation = Propagation.NEVER)
@Import({GenreServiceImpl.class, JpaGenreRepository.class, GenreConverter.class,
        BookServiceImpl.class, JpaBookRepository.class, BookConverter.class,
        JpaAuthorRepository.class, AuthorConverter.class})
public class GenreServiceTest {

    @Autowired
    private GenreService genreService;

    @Autowired
    private BookService bookService;

    @DisplayName("при удалении жанра так же должен удалять жанр из книг")
    @ParameterizedTest
    @MethodSource("getGenreIds")
    void shouldRemoveFromBookGenresAfterDeletingGenre(long id) {
        genreService.deleteById(id);
        bookService.findAll().forEach(book -> {
            var genreIds = book.getGenreDtos().stream().map(GenreDto::getId).collect(Collectors.toSet());
            assertThat(genreIds).doesNotContain(1L);
        });
    }

    private static long[] getGenreIds() {
        return LongStream.range(1, 7).toArray();
    }

}
