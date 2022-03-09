package org.ssau.privatechannel.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ssau.privatechannel.model.AuthorizationKey;
import org.ssau.privatechannel.repository.AuthKeyRepository;

@Service
public class AuthKeyService {

    private final AuthKeyRepository authKeyRepository;

    @Autowired
    public AuthKeyService(AuthKeyRepository authKeyRepository) {
        this.authKeyRepository = authKeyRepository;
    }


    public AuthorizationKey get() {
        return authKeyRepository.get();
    }

    public void set(AuthorizationKey info) {
        authKeyRepository.set(info);
    }

    public void delete(AuthorizationKey info) {
        authKeyRepository.delete(info);
    }
}
