package ru.otus.hw.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "text")
    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Override
    public boolean equals(Object other) {
        if (other == null || getClass() != other.getClass()) return false;
        Comment comment = (Comment) other;
        return getId() == comment.getId()
                && Objects.equals(getText(), comment.getText())
                && getBook().getId() == comment.getBook().getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getText(), getBook().getId());
    }

    @Override
    public String toString() {
        return "Comment(id=" + getId() + ", text=" + getText() + ", bookId=" + getBook().getId() + ")";
    }
}
