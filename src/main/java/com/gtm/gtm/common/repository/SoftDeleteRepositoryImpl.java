package com.gtm.gtm.common.repository;

import com.gtm.gtm.common.domain.SoftDeletable;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import java.time.OffsetDateTime;

public class SoftDeleteRepositoryImpl<T extends SoftDeletable, ID>
        extends SimpleJpaRepository<T, ID>
        implements SoftDeleteRepository<T, ID> {

    @PersistenceContext
    private final EntityManager em;

    private final Class<T> domainClass;

    public SoftDeleteRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager em) {
        super(entityInformation, em);
        this.em = em;
        this.domainClass = entityInformation.getJavaType();
    }

    @Override @Transactional
    public void deleteById(ID id) { softDeleteById(id); }

    @Override @Transactional
    public void delete(T entity) {
        entity.setDeleted(true);
        entity.setDeletedAt(OffsetDateTime.now());
        save(entity);
    }

    @Override @Transactional
    public void deleteAll(Iterable<? extends T> entities) {
        var now = OffsetDateTime.now();
        for (T e : entities) { e.setDeleted(true); e.setDeletedAt(now); }
        saveAll(entities);
    }

    @Override @Transactional
    public void softDeleteById(ID id) {
        var e = findById(id).orElseThrow();
        e.setDeleted(true);
        e.setDeletedAt(OffsetDateTime.now());
        save(e);
    }

    @Override
    @Transactional
    public void restoreById(ID id) {
        em.createQuery(
                        "update " + domainClass.getName() +
                                " e set e.deleted = false, e.deletedAt = null, e.updatedAt = CURRENT_TIMESTAMP " +
                                "where e.id = :id"
                )
                .setParameter("id", id)
                .executeUpdate();
    }
}
