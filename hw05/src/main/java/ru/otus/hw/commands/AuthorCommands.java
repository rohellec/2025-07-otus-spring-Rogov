package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.converters.AuthorConverter;
import ru.otus.hw.services.AuthorService;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@ShellComponent
public class AuthorCommands {

    private final AuthorService authorService;

    private final AuthorConverter authorConverter;

    @ShellMethod(value = "Find all authors", key = "aa")
    public String findAllAuthors() {
        return authorService.findAll().stream()
                .map(authorConverter::authorToString)
                .collect(Collectors.joining("," + System.lineSeparator()));
    }

    @ShellMethod(value = "Find author by id", key = "abid")
    public String findAuthorById(long id) {
        return authorService.findById(id)
                .map(authorConverter::authorToString)
                .orElse("Book with id %d not found".formatted(id));
    }

    // ains fullName
    @ShellMethod(value = "Insert author", key = "ains")
    public String insertAuthor(String fullName) {
        var savedAuthor = authorService.insert(fullName);
        return authorConverter.authorToString(savedAuthor);
    }

    // aupd 1 newName
    @ShellMethod(value = "Update author", key = "aupd")
    public String updateAuthor(long id, String fullName) {
        var savedAuthor = authorService.update(id, fullName);
        return authorConverter.authorToString(savedAuthor);
    }

    // adel 1
    @ShellMethod(value = "Delete author by id", key = "adel")
    public void deleteAuthor(long id) {
        authorService.deleteById(id);
    }
}
