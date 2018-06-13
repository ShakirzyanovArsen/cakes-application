package com.cakes.cakes.repository;

import com.cakes.cakes.domain.Cake;
import com.cakes.cakes.domain.CakeFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Repository
@Transactional
public class DefaultCakeRepository implements CakeRepository {


    @PersistenceContext(type = PersistenceContextType.EXTENDED)
    private EntityManager entityManager;


    @Override
    @Async
    public CompletableFuture<Cake> getItem(Long id) {
        return CompletableFuture.supplyAsync(() -> entityManager.find(Cake.class, id));
    }

    @Override
    @Async
    public CompletableFuture<List<Cake>> getRange(CakeFilter filter) {
        return CompletableFuture.supplyAsync(() -> {
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Cake> query = builder.createQuery(Cake.class);
            Root<Cake> root = query.from(Cake.class);
            query = query.select(root);
            if (filter.getText() != null) {
                query = query
                        .where(builder.like(root.get("name"), "%" + filter.getText() + "%"));
            }
            if (filter.getStatuses().length > 0) {
                Path<String> exp = root.get("status");
                query = query.where(exp.in(filter.getStatuses()));
            }
            return entityManager.createQuery(query)
                    .setMaxResults(filter.getLimit())
                    .setFirstResult((filter.getPage() - 1) * filter.getLimit())
                    .getResultList();
        });
    }

    @Async
    @Transactional
    @Override
    public CompletableFuture<Void> addItem(Cake item) {
        entityManager.persist(item);
        return CompletableFuture.completedFuture(null);
    }

    @Async
    @Transactional
    @Override
    public CompletableFuture<Void> updateItem(Cake item) {
        entityManager.merge(item);
        entityManager.flush();
        return CompletableFuture.completedFuture(null);
    }

    @Transactional
    @Async
    @Override
    public CompletableFuture<Void> removeItem(Cake item) {
        entityManager.remove(item);
        entityManager.flush();
        return CompletableFuture.completedFuture(null);
    }

    @Override
    @Async
    @Transactional
    public CompletableFuture<Long> getTotal(CakeFilter filter) {
        return CompletableFuture.supplyAsync(() -> {
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Long> query = builder.createQuery(Long.class);
            query = query.select(builder.count(query.from(Cake.class)));
            Root<Cake> root = query.from(Cake.class);
            if (filter.getText() != null) {
                query = query
                        .where(builder.like(root.get("name"), "%" + filter.getText() + "%"));
            }
            if (filter.getStatuses().length > 0) {
                Path<String> exp = root.get("status");
                query = query.where(exp.in(filter.getStatuses()));
            }
            return entityManager.createQuery(query).getSingleResult();
        });
    }
}
