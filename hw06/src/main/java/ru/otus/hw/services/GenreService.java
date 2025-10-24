package ru.otus.hw.services;

import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface GenreService {
    Optional<Genre> findById(long id);

    List<Genre> findAll();

    List<Genre> findAllByIds(Set<Long> ids);

    Genre insert(String title);

    Genre update(long id, String title);

    void deleteById(long id);
}
