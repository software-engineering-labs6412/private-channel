package org.ssau.privatechannel.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ssau.privatechannel.constants.Parameters;
import org.ssau.privatechannel.model.AuthorizationKey;
import org.ssau.privatechannel.repository.AuthKeyRepository;

import java.util.Objects;
import java.util.Random;

@Service
public class AuthKeyService {

    private final AuthKeyRepository authKeyRepository;

    private static final Random RANDOMIZER = new Random();

    @Autowired
    public AuthKeyService(AuthKeyRepository authKeyRepository) {
        this.authKeyRepository = authKeyRepository;
    }

    public AuthorizationKey get() {
        return authKeyRepository.get();
    }

    public void set(AuthorizationKey key) {
        if (Objects.isNull(key.getId())) {
            key.setId(Math.abs(RANDOMIZER.nextLong()) % Parameters.MAX_ID);
        }
        authKeyRepository.set(key);
    }

    public void delete(AuthorizationKey info) {
        authKeyRepository.delete(info);
    }

    public boolean isActual(String key) {
        AuthorizationKey actualKey = authKeyRepository.get();
        AuthorizationKey expectedKey = new AuthorizationKey(actualKey.getId(), key);
        return actualKey.equals(expectedKey);
    }
}
