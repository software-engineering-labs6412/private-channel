package org.ssau.privatechannel.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ssau.privatechannel.model.ReceivedInformation;
import org.ssau.privatechannel.repository.ReceivedInfoRepository;

import java.util.Collection;
import java.util.List;

@Service
public class ReceivedInfoService {

    private final ReceivedInfoRepository infoRepository;

    @Autowired
    public ReceivedInfoService(ReceivedInfoRepository infoRepository) {
        this.infoRepository = infoRepository;
    }

    public Collection<ReceivedInformation> findAll() {
        return infoRepository.findAll();
    }

    public Collection<ReceivedInformation> findAllByIds(List<Long> ids) {
        return infoRepository.findAllByIds(ids);
    }

    public Collection<ReceivedInformation> nextBatch() {
        return infoRepository.nextBatch();
    }

    public void deleteBatch(Collection<ReceivedInformation> batch) {
        infoRepository.deleteBatch(batch);
    }

    public void add(ReceivedInformation info) {
        infoRepository.add(info);
    }

    public void addAll(List<ReceivedInformation> info) {
        infoRepository.addAll(info);
    }

    public void delete(ReceivedInformation info) {
        infoRepository.delete(info);
    }
}
