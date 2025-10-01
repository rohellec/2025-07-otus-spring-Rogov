package ru.otus.hw.repositories;

import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface GenreRepository {
    Optional<Genre> findById(long id);

    List<Genre> findAll();

    List<Genre> findAllByIds(Set<Long> ids);

    Genre save(Genre genre);

    void deleteById(long id);
}
