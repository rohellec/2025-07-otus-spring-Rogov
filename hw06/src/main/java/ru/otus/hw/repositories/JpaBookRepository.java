package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;

import java.util.*;

import static org.springframework.data.jpa.repository.EntityGraph.EntityGraphType.FETCH;

@Repository
public class JpaBookRepository implements BookRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<Book> findById(long id) {
        var entitiesCombinedGraph = em.getEntityGraph("authors-entity-graph");
        TypedQuery<Book> query = em.createQuery("select b from Book b where id = :bookId", Book.class);
        query.setParameter("bookId", id);
        query.setHint(FETCH.getKey(), entitiesCombinedGraph);
        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    @Override
    public List<Book> findAll() {
        var entitiesCombinedGraph = em.getEntityGraph("authors-entity-graph");
        TypedQuery<Book> query = em.createQuery("select b from Book b", Book.class);
        query.setHint(FETCH.getKey(), entitiesCombinedGraph);
        return query.getResultList();
    }

    @Override
    public Book save(Book book) {
        if (book.getAuthor() == null) {
            throw new EntityNotFoundException("Author is not specified");
        }
        if (book.getId() == 0) {
            em.persist(book);
            return book;
        }
        Book persisted = em.find(Book.class, book.getId());
        if (persisted != null) {
            return em.merge(book);
        } else {
            throw new EntityNotFoundException("Book with id " + book.getId() + " not found");
        }
    }

    @Override
    public void deleteById(long id) {
        Book book = em.find(Book.class, id);
        if (book != null)
            em.remove(book);
    }
}
