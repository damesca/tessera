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

/** A JPA implementation of {@link InteractivePrivacyEndpointDAO} */
public class InteractivePrivacyEndpointDAOImpl implements InteractivePrivacyEndpointDAO {
    
    private static final Logger LOGGER =
    LoggerFactory.getLogger(InteractivePrivacyEndpointDAOImpl.class);

    private final EntityManagerTemplate entityManagerTemplate;

    public InteractivePrivacyEndpointDAOImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManagerTemplate = new EntityManagerTemplate(entityManagerFactory);
    }

    @Override
    public InteractivePrivacyEndpoint save(final InteractivePrivacyEndpoint entity) {
        /*LOGGER.debug(
            "Persisting InteractivePrivacyEndpoint with hash {} and port {}",
            entity.getId(),
            entity.getPort()
        );*/

        return entityManagerTemplate.execute(
            entityManager -> {
                entityManager.persist(entity);
                return entity;
        });
    }

    @Override
    public Optional<InteractivePrivacyEndpoint> findById(byte[] id) {
        LOGGER.debug("Retrieving endpoint with Id {}", id);

        InteractivePrivacyEndpoint interactivePrivacyEndpoint =
            entityManagerTemplate.execute(
                entityManager -> entityManager.find(InteractivePrivacyEndpoint.class, id));
    
        return Optional.ofNullable(interactivePrivacyEndpoint);
    }
    /*
    @Override
    public void delete(final MessageHash hash) {
      LOGGER.info("Deleting endpoint with hash {}", hash);
      entityManagerTemplate.execute(
          entityManager -> {
            InteractivePrivacyEndpoint txn = entityManager.find(InteractivePrivacyEndpoint.class, hash);
            if (txn == null) {
              throw new EntityNotFoundException();
            }
  
            entityManager
                .createNamedQuery("InteractivePrivacyEndpoint.DeleteByHash")
                .setParameter("hash", hash.getHashBytes())
                .executeUpdate();
  
            return txn;
          });
    }
    */

}
