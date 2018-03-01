package org.superbiz.moviefun.blobstore;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.superbiz.moviefun.movies.Movie;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class MySQLStore implements BlobStore{

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void put(Blob blob) throws IOException {
        entityManager.persist(blob);
    }

    @Override
    public Optional<Blob> get(String name) throws IOException {
        Blob blobResult = (Blob) entityManager.createNamedQuery("Blob.findByName").setParameter("1", name).getResultList().get(0);
        if (blobResult == null) {
            return Optional.empty();
        }
        return Optional.of(new Blob(blobResult));
    }

    @Override
    public void deleteAll() {

    }
}
