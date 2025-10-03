package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.models.Author;
import ru.otus.hw.repositories.AuthorRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    @Override
    public Optional<Author> findById(long id) {
        return authorRepository.findById(id);
    }

    @Override
    public List<Author> findAll() {
        return authorRepository.findAll();
    }

    @Override
    public Author insert(String fullName) {
        return save(0, fullName);
    }

    @Override
    public Author update(long id, String fullName) {
        return save(id, fullName);
    }

    @Override
    public void deleteById(long id) {
        authorRepository.deleteById(id);
    }

    private Author save(long id, String fullName) {
        Author author = new Author(id, fullName);
        return authorRepository.save(author);
    }
}
