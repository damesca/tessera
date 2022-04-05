package com.quorum.tessera.data.internal;

import com.quorum.tessera.data.*;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** A JPA implementation of {@link PrivateOutputDAO} */
public class PrivateOutputDAOImpl implements PrivateOutputDAO {
    
    private static final Logger LOGGER =
    LoggerFactory.getLogger(PrivateOutputDAOImpl.class);

    private final EntityManagerTemplate entityManagerTemplate;

    public PrivateOutputDAOImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManagerTemplate = new EntityManagerTemplate(entityManagerFactory);
    }

    @Override
    public PrivateOutputEntity save(final PrivateOutputEntity entity) {

        return entityManagerTemplate.execute(
            entityManager -> {
                entityManager.persist(entity);
                return entity;
        });
    }

    @Override
    public Optional<PrivateOutputEntity> findById(String id) {

        PrivateOutputEntity privateOutputEntity =
            entityManagerTemplate.execute(
                entityManager -> entityManager.find(PrivateOutputEntity.class, id));
    
        return Optional.ofNullable(privateOutputEntity);
    }

}
