package org.ssau.privatechannel.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.ssau.privatechannel.model.AuthorizationKey;

@Slf4j
@Repository
public class AuthKeyRepository extends AbstractRepository {

    public AuthorizationKey get() {
        try {
            return entityManager.createNamedQuery(QueryNames.GET, AuthorizationKey.class).getSingleResult();
        }
        catch (Exception e) {
            log.warn("Header key not placed in database");
            return null;
        }
    }

    @Transactional
    public void set(AuthorizationKey info) {
        entityManager.persist(info);
    }

    @Transactional
    public void delete(AuthorizationKey info) {
        entityManager.remove(info);
    }

    private static abstract class QueryNames {
        public static final String GET = "AuthorizationKey.get";
    }
}
