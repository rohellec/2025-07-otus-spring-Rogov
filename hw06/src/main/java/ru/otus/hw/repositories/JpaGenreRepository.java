package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public class JpaGenreRepository implements GenreRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<Genre> findById(long id) {
        Genre genre = em.find(Genre.class, id);
        return Optional.ofNullable(genre);
    }

    @Override
    public List<Genre> findAll() {
        TypedQuery<Genre> query = em.createQuery("select g from Genre g", Genre.class);
        return query.getResultList();
    }

    @Override
    public List<Genre> findAllByIds(Set<Long> ids) {
        String jpql = "select g from Genre g where id IN :ids";
        TypedQuery<Genre> query = em.createQuery(jpql, Genre.class);
        query.setParameter("ids", ids);
        return query.getResultList();
    }

    @Override
    public Genre save(Genre genre) {
        if (genre.getId() == 0) {
            em.persist(genre);
            return genre;
        }
        Genre persisted = em.find(Genre.class, genre.getId());
        if (persisted != null) {
            return em.merge(genre);
        } else {
            throw new EntityNotFoundException("Genre with id " + genre.getId() + " not found");
        }
    }

    @Override
    public void deleteById(long id) {
        Genre genre = em.find(Genre.class, id);
        if (genre != null) {
            em.remove(genre);
        }
    }
}
