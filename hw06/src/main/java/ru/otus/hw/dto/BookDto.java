package ru.otus.hw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDto {

    private long id;

    private String title;

    private AuthorDto authorDto;

    private List<GenreDto> genreDtos = new ArrayList<>();

    @Override
    public boolean equals(Object other) {
        if (other == null || getClass() != other.getClass()) return false;
        BookDto book = (BookDto) other;
        if (this.getId() != book.getId()) return false;

        String title = this.getTitle();
        String otherTitle = book.getTitle();
        if (title == null && otherTitle != null) return false;
        if (title != null && !title.equals(otherTitle))  return false;

        AuthorDto authorDto = this.getAuthorDto();
        AuthorDto otherAuthorDto = book.getAuthorDto();
        if (authorDto == null && otherAuthorDto != null) return false;
        return authorDto == null || (otherAuthorDto != null && authorDto.getId() == otherAuthorDto.getId());
    }

    @Override
    public int hashCode() {
        var genreDtos = this.getGenreDtos() != null ? this.getGenreDtos().toArray() : null;
        return Objects.hash(id, title, authorDto, Arrays.hashCode(genreDtos));
    }
}
