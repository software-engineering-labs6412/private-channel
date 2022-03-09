package org.ssau.privatechannel.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.ssau.privatechannel.model.AuthorizationKey;

@Repository
public class AuthKeyRepository extends AbstractRepository {

    public AuthorizationKey get() {
        return entityManager.createNamedQuery("AuthorizationKey.get",
                AuthorizationKey.class).getSingleResult();
    }

    @Transactional
    public void set(AuthorizationKey info) {
        entityManager.persist(info);
    }

    @Transactional
    public void delete(AuthorizationKey info) {
        entityManager.remove(info);
    }
}
