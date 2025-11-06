package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaAuthorRepository implements AuthorRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<Author> findById(long id) {
        var author = em.find(Author.class, id);
        return Optional.ofNullable(author);
    }

    @Override
    public List<Author> findAll() {
        TypedQuery<Author> query = em.createQuery("select a from Author a", Author.class);
        return query.getResultList();
    }

    @Override
    public Author save(Author author) {
        if (author.getId() == 0) {
            em.persist(author);
            return author;
        }
        Author persisted = em.find(Author.class, author.getId());
        if (persisted != null) {
            return em.merge(author);
        } else {
            throw new EntityNotFoundException("Author with id " + author.getId() + " not found");
        }
    }

    @Override
    public void deleteById(long id) {
        var author = em.find(Author.class, id);
        if (author != null) {
            em.remove(author);
        }
    }
}
