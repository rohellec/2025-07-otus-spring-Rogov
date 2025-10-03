package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class GenreServiceImpl implements GenreService {
    private final GenreRepository genreRepository;

    @Override
    public Optional<Genre> findById(long id) {
        return genreRepository.findById(id);
    }

    @Override
    public List<Genre> findAll() {
        return genreRepository.findAll();
    }

    @Override
    public List<Genre> findAllByIds(Set<Long> ids) {
        return genreRepository.findAllByIds(ids);
    }

    @Override
    public Genre insert(String name) {
        return save(0, name);
    }

    @Override
    public Genre update(long id, String name) {
        return save(id, name);
    }

    @Override
    public void deleteById(long id) {
        genreRepository.deleteById(id);
    }

    private Genre save(long id, String name) {
        Genre genre = new Genre(id, name);
        return genreRepository.save(genre);
    }
}
