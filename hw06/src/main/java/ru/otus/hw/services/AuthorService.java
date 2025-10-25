package ru.otus.hw.services;

import ru.otus.hw.dto.AuthorDto;

import java.util.List;
import java.util.Optional;

public interface AuthorService {
    Optional<AuthorDto> findById(long id);

    List<AuthorDto> findAll();

    AuthorDto insert(String fullName);

    AuthorDto update(long id, String fullName);

    void deleteById(long id);
}
