package org.ssau.privatechannel.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ssau.privatechannel.model.ConfidentialInfo;
import org.ssau.privatechannel.repository.ConfidentialInfoRepository;

import java.util.Collection;
import java.util.List;

@Service
public class ConfidentialInfoService {

    private final ConfidentialInfoRepository infoRepository;

    @Autowired
    public ConfidentialInfoService(ConfidentialInfoRepository infoRepository) {
        this.infoRepository = infoRepository;
    }

    public Collection<ConfidentialInfo> findAll() {
        return infoRepository.findAll();
    }

    public Collection<ConfidentialInfo> findAllByIds(List<Long> ids) {
        return infoRepository.findAllByIds(ids);
    }

    public Collection<ConfidentialInfo> nextBatch() {
        return infoRepository.nextBatch();
    }

    public void deleteBatch(Collection<ConfidentialInfo> batch) {
        infoRepository.deleteBatch(batch);
    }

    public void add(ConfidentialInfo info) {
        infoRepository.add(info);
    }

    public void addAll(List<ConfidentialInfo> info) {
        infoRepository.addAll(info);
    }

    public void delete(ConfidentialInfo info) {
        infoRepository.delete(info);
    }

}
