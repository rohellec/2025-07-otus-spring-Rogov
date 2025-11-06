package ru.otus.hw.dto;

import lombok.*;

import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {

    private long id;

    private String text;

    private long bookId;

    @Override
    public boolean equals(Object other) {
        if (other == null || getClass() != other.getClass()) return false;
        CommentDto commentDto = (CommentDto) other;
        return getId() == commentDto.getId()
                && Objects.equals(getText(), commentDto.getText())
                && getBookId() == commentDto.getBookId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getText(), getBookId());
    }

    @Override
    public String toString() {
        return "Comment(id=" + getId() + ", text=" + getText() + ", bookId=" + getBookId() + ")";
    }
}
