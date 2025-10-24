package ru.otus.hw.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final CommentConverter commentConverter;

    private final BookRepository bookRepository;

    public CommentServiceImpl(CommentRepository commentRepository,
                              CommentConverter commentConverter,
                              BookRepository bookRepository) {
        this.commentRepository = commentRepository;
        this.commentConverter = commentConverter;
        this.bookRepository = bookRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> findAllByBookId(long bookId) {
        return commentRepository.findAllByBookId(bookId).stream()
                .map(commentConverter::commentToDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CommentDto> findById(long id) {
        return commentRepository.findById(id)
                .map(commentConverter::commentToDto);
    }

    @Override
    @Transactional
    public CommentDto insert(String text, long bookId) {
        return save(0, text, bookId);
    }

    @Override
    @Transactional
    public CommentDto update(long id, String text, long bookId) {
        return save(id, text, bookId);
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        commentRepository.deleteById(id);
    }

    private CommentDto save(long id, String title, long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %d not found".formatted(bookId)));
        Comment comment = commentRepository.save(new Comment(id, title, book));
        return commentConverter.commentToDto(comment);
    }
}
