package ru.otus.hw.converters;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

@Component
public class CommentConverter {
    public String commentToString(Comment comment) {
        Book book = comment.getBook();
        long bookId = book != null ?  book.getId() : 0;
        return "Id: %d, text: %s, bookId: %d".formatted(
                comment.getId(),
                comment.getText(),
                bookId
        );
    }

    public String commentToString(CommentDto commentDto) {
        return "Id: %d, text: %s, bookId: %d".formatted(
                commentDto.getId(),
                commentDto.getText(),
                commentDto.getBookId()
        );
    }

    public CommentDto commentToDto(Comment comment) {
        long bookId = comment.getBook() != null ? comment.getBook().getId() : 0;
        return new CommentDto(comment.getId(), comment.getText(), bookId);
    }
}
