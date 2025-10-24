package ru.otus.hw.converters;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.models.Author;

@Component
public class AuthorConverter {
    public String authorToString(Author author) {
        return "Id: %d, FullName: %s".formatted(author.getId(), author.getFullName());
    }

    public String authorToString(AuthorDto authorDto) {
        return "Id: %d, FullName: %s".formatted(authorDto.getId(), authorDto.getFullName());
    }

    public AuthorDto authorToDto(Author author) {
        return new AuthorDto(author.getId(), author.getFullName());
    }
}
