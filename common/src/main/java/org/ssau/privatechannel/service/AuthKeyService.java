package org.ssau.privatechannel.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ssau.privatechannel.constants.Parameters;
import org.ssau.privatechannel.constants.SystemProperties;
import org.ssau.privatechannel.model.AuthorizationKey;
import org.ssau.privatechannel.repository.AuthKeyRepository;
import org.ssau.privatechannel.utils.SystemContext;

import java.util.Objects;
import java.util.Random;

@Slf4j
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

    public void generateNewHeaderKey() {
        String headerKey = SystemContext.getProperty(SystemProperties.HEADER_KEY);

        AuthorizationKey previousKey = get();
        if (Objects.isNull(previousKey))
            log.warn("Auth service returned empty result. Key will be created");
        else
            delete(previousKey);

        AuthorizationKey newKey = new AuthorizationKey(null, headerKey);
        set(newKey);
    }

}
