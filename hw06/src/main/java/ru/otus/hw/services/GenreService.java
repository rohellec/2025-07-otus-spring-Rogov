package ru.otus.hw.services;

import ru.otus.hw.dto.GenreDto;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface GenreService {
    Optional<GenreDto> findById(long id);

    List<GenreDto> findAll();

    List<GenreDto> findAllByIds(Set<Long> ids);

    GenreDto insert(String title);

    GenreDto update(long id, String title);

    void deleteById(long id);
}
