package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.converters.GenreConverter;
import ru.otus.hw.services.GenreService;

import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings({"SpellCheckingInspection", "unused"})
@RequiredArgsConstructor
@ShellComponent
public class GenreCommands {

    private final GenreService genreService;

    private final GenreConverter genreConverter;

    @ShellMethod(value = "Find all genres", key = "ag")
    public String findAllGenres() {
        return genreService.findAll().stream()
                .map(genreConverter::genreToString)
                .collect(Collectors.joining("," + System.lineSeparator()));
    }

    @ShellMethod(value = "Find genre by id", key = "gbid")
    public String findGenreById(long id) {
        return genreService.findById(id)
                .map(genreConverter::genreToString)
                .orElse("Genre with id %d not found".formatted(id));
    }

    // agbids 2,3
    @ShellMethod(value = "Find all genres by ids", key = "agbids")
    public String findAllGenres(Set<Long> ids) {
        return genreService.findAll().stream()
                .map(genreConverter::genreToString)
                .collect(Collectors.joining("," + System.lineSeparator()));
    }

    // gins name
    @ShellMethod(value = "Insert genre", key = "gins")
    public String insertGenre(String name) {
        var savedGenre = genreService.insert(name);
        return genreConverter.genreToString(savedGenre);
    }

    // gupd 1 newName
    @ShellMethod(value = "Update genre", key = "gupd")
    public String updateGenre(long id, String name) {
        var savedGenre = genreService.update(id, name);
        return genreConverter.genreToString(savedGenre);
    }

    // gdel 1
    @ShellMethod(value = "Delete genre by id", key = "gdel")
    public void deleteAuthor(long id) {
        genreService.deleteById(id);
    }
}
