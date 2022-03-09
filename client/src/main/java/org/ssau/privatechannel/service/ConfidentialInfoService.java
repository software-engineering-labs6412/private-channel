package org.ssau.privatechannel.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ssau.privatechannel.model.ConfidentialInformation;
import org.ssau.privatechannel.repository.ConfidentialInformationRepository;

import java.util.Collection;

@Service
public class ConfidentialInfoService {

    private final ConfidentialInformationRepository infoRepository;

    @Autowired
    public ConfidentialInfoService(ConfidentialInformationRepository infoRepository) {
        this.infoRepository = infoRepository;
    }

    public Collection<ConfidentialInformation> findAll() {
        return infoRepository.findAll();
    }

    public Collection<ConfidentialInformation> nextBatch() {
        return infoRepository.nextBatch();
    }

    public void deleteBatch(Collection<ConfidentialInformation> batch) {
        infoRepository.deleteBatch(batch);
    }

    public void add(ConfidentialInformation info) {
        infoRepository.add(info);
    }

    public void delete(ConfidentialInformation info) {
        infoRepository.delete(info);
    }

}
