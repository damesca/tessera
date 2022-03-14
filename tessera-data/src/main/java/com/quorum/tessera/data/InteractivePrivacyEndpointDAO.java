package com.quorum.tessera.data;

import java.util.Optional;
import java.util.ServiceLoader;
//import java.util.List;

public interface InteractivePrivacyEndpointDAO {
    
    InteractivePrivacyEndpoint save(InteractivePrivacyEndpoint entity);

    Optional<InteractivePrivacyEndpoint> findById(MessageHash id);

    //Optional<InteractivePrivacyEndpoint> retrieveByHash(MessageHash hash);

    //void delete(MessageHash hash);

    static InteractivePrivacyEndpointDAO create() {
        return ServiceLoader.load(InteractivePrivacyEndpointDAO.class).findFirst().get();
    }

}
