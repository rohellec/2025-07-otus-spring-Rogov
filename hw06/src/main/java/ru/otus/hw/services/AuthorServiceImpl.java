package ru.otus.hw.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.converters.AuthorConverter;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.repositories.AuthorRepository;

import java.util.List;
import java.util.Optional;

@Service
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    private final AuthorConverter authorConverter;

    public AuthorServiceImpl(AuthorRepository authorRepository, AuthorConverter authorConverter) {
        this.authorRepository = authorRepository;
        this.authorConverter = authorConverter;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AuthorDto> findById(long id) {
        return authorRepository.findById(id)
                .map(authorConverter::authorToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuthorDto> findAll() {
        return authorRepository.findAll().stream()
                .map(authorConverter::authorToDto).toList();
    }

    @Override
    @Transactional
    public AuthorDto insert(String fullName) {
        return save(0, fullName);
    }

    @Override
    @Transactional
    public AuthorDto update(long id, String fullName) {
        return save(id, fullName);
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        authorRepository.deleteById(id);
    }

    private AuthorDto save(long id, String fullName) {
        Author author = authorRepository.save(new Author(id, fullName));
        return authorConverter.authorToDto(author);
    }
}
