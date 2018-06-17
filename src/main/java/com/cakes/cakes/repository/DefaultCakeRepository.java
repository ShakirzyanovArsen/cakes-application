package com.cakes.cakes.repository;

import com.cakes.cakes.domain.Cake;
import com.cakes.cakes.domain.CakeFilter;
import com.cakes.cakes.domain.StatusType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.*;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Repository
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
            final List<Predicate> predicates = new ArrayList<>();
            if (filter.getText() != null) {
                Predicate predicate = builder.like(root.get("name"), "%" + filter.getText() + "%");
                predicates.add(predicate);
            }
            if (filter.getStatuses().length > 0) {
                Path<String> exp = root.get("status");
                Predicate predicate = exp.in(filter.getStatuses());
                predicates.add(predicate);
            }
            Predicate and = builder.and(predicates.toArray(new Predicate[predicates.size()]));
            query = query.where(and);
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
        return CompletableFuture.completedFuture(null);
    }

    @Transactional
    @Async
    @Override
    public CompletableFuture<Void> removeItem(Cake item) {
        entityManager.remove(item);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    @Async
    @Transactional
    public CompletableFuture<Long> getTotal(CakeFilter filter) {
        return CompletableFuture.supplyAsync(() -> {
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Long> query = builder.createQuery(Long.class);
            Root<Cake> root = query.from(Cake.class);
            query.select(builder.count(root));
            final List<Predicate> predicates = new ArrayList<>();
            if (filter.getText() != null) {
                Predicate predicate = builder.like(root.get("name"), "%" + filter.getText() + "%");
                predicates.add(predicate);
            }
            if (filter.getStatuses().length > 0) {
                Path<String> exp = root.get("status");
                Predicate predicate = exp.in(filter.getStatuses());
                predicates.add(predicate);
            }
            Predicate and = builder.and(predicates.toArray(new Predicate[predicates.size()]));
            query = query.where(and);
            return entityManager.createQuery(query).getSingleResult();
        });
    }
}
