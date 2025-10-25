package ru.otus.hw.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.repositories.JpaBookRepository;
import ru.otus.hw.repositories.JpaCommentRepository;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.CommentServiceImpl;

import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThatNoException;

@DisplayName("Сервис для работы с комментариями ")
@DataJpaTest
@Transactional(propagation = Propagation.NEVER)
@Import({CommentServiceImpl.class, CommentConverter.class, JpaCommentRepository.class, JpaBookRepository.class})
public class CommentServiceTest {

    private static final long FIRST_BOOK_ID = 1L;
    private static final long FIRST_COMMENT_ID = 1L;

    @Autowired
    private CommentService commentService;

    @DisplayName("не должен бросать LazyInitializationException при запросе комментария по id")
    @ParameterizedTest
    @MethodSource("getCommentIds")
    void shouldNotThrowExceptionWhileGettingById(long id) {
        assertThatNoException().isThrownBy(() -> commentService.findById(id));
    }

    @DisplayName("не должен бросать LazyInitializationException при запросе всех комментариев для книги")
    @Test
    void shouldNotThrowExceptionWhileGettingAllByBookId() {
        assertThatNoException().isThrownBy(() -> commentService.findAllByBookId(FIRST_BOOK_ID));
    }

    @DisplayName("не должен бросать LazyInitializationException при создании комментария")
    @Test
    void shouldNotThrowExceptionWhileCreatingComment() {
        assertThatNoException().isThrownBy(
                () -> commentService.insert("NewComment_99", FIRST_BOOK_ID));

    }

    @DisplayName("не должен бросать LazyInitializationException при обновлении комментария")
    @Test
    void shouldNotThrowExceptionWhileUpdatingComment() {
        assertThatNoException().isThrownBy(
                () -> commentService.update(FIRST_COMMENT_ID, "UpdatedComment_100500", FIRST_BOOK_ID));

    }

    @Transactional
    @DisplayName("не должен бросать LazyInitializationException при удалении комментария по id")
    @ParameterizedTest
    @MethodSource("getCommentIds")
    void shouldNotThrowExceptionWhileDeletingById(long id) {
        assertThatNoException().isThrownBy(() -> commentService.deleteById(id));
    }

    private static long[] getCommentIds() {
        return LongStream.range(1, 7).toArray();
    }
}
