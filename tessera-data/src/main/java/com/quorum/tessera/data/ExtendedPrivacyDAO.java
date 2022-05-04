package com.quorum.tessera.data;

import java.util.Optional;
import java.util.ServiceLoader;

public interface ExtendedPrivacyDAO {
    
    ExtendedPrivacyEntity save(ExtendedPrivacyEntity entity);

    Optional<ExtendedPrivacyEntity> retrieve(String key);

    static ExtendedPrivacyDAO create() {
        return ServiceLoader.load(ExtendedPrivacyDAO.class).findFirst().get();
    }

}
