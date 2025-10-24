package ru.otus.hw.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "books")
@NamedEntityGraph(name = "authors-entity-graph",
        attributeNodes = {@NamedAttributeNode("author")}
)
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "title")
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private Author author;

    @Fetch(FetchMode.SUBSELECT)
    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "books_genres",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
        )
    private List<Genre> genres = new ArrayList<>();

    public void addGenre(Genre genre) {
        genres.add(genre);
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || getClass() != other.getClass()) return false;
        Book book = (Book) other;
        if (this.getId() != book.getId()) return false;

        String title = this.getTitle();
        String otherTitle = book.getTitle();
        if (title == null && otherTitle != null) return false;
        if (title != null && !title.equals(otherTitle)) return false;

        Author author = this.getAuthor();
        Author otherAuthor = book.getAuthor();
        if (author == null && otherAuthor != null) return false;
        return author == null || (otherAuthor != null && author.getId() == otherAuthor.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getTitle(), getAuthor().getId());
    }
}
