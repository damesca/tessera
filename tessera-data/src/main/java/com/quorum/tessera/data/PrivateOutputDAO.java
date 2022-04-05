package com.quorum.tessera.data;

import java.util.Optional;
import java.util.ServiceLoader;

public interface PrivateOutputDAO {

    PrivateOutputEntity save(PrivateOutputEntity entity);

    Optional<PrivateOutputEntity> findById(String id);

    static PrivateOutputDAO create() {
        return ServiceLoader.load(PrivateOutputDAO.class).findFirst().get();
    }

}