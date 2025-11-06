package ru.otus.hw.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.converters.GenreConverter;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;

    private final GenreConverter genreConverter;

    public GenreServiceImpl(GenreRepository genreRepository, GenreConverter genreConverter) {
        this.genreRepository = genreRepository;
        this.genreConverter = genreConverter;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<GenreDto> findById(long id) {
        return genreRepository.findById(id)
                .map(genreConverter::genreToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GenreDto> findAll() {
        return genreRepository.findAll().stream()
                .map(genreConverter::genreToDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<GenreDto> findAllByIds(Set<Long> ids) {
        return genreRepository.findAllByIds(ids).stream()
                .map(genreConverter::genreToDto)
                .toList();
    }

    @Override
    @Transactional
    public GenreDto insert(String name) {
        return save(0, name);
    }

    @Override
    @Transactional
    public GenreDto update(long id, String name) {
        return save(id, name);
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        genreRepository.deleteById(id);
    }

    private GenreDto save(long id, String name) {
        Genre genre = genreRepository.save(new Genre(id, name));
        return genreConverter.genreToDto(genre);
    }
}
