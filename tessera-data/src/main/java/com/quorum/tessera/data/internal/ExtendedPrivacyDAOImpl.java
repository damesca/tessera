package com.quorum.tessera.data.internal;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import com.quorum.tessera.data.EntityManagerTemplate;
import com.quorum.tessera.data.ExtendedPrivacyDAO;
import com.quorum.tessera.data.ExtendedPrivacyEntity;
import jakarta.persistence.EntityManagerFactory;

public class ExtendedPrivacyDAOImpl implements ExtendedPrivacyDAO {

    private EntityManagerTemplate entityManagerTemplate;

    public ExtendedPrivacyDAOImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManagerTemplate = new EntityManagerTemplate(entityManagerFactory);
    }

    @Override
    public ExtendedPrivacyEntity save(final ExtendedPrivacyEntity entity) {
        // TODO: perform a safe saving for data, checking if primary ID already exists
        return entityManagerTemplate.execute(
            entityManager -> {
            entityManager.persist(entity);
            return entity;
        });
    }

    @Override
    public Optional<ExtendedPrivacyEntity> retrieve(String key) {
      
        ExtendedPrivacyEntity extendedPrivacyEntity = 
            entityManagerTemplate.execute(
                entityManager -> entityManager.find(ExtendedPrivacyEntity.class, key));
        
        return Optional.ofNullable(extendedPrivacyEntity);
    }
    
}
