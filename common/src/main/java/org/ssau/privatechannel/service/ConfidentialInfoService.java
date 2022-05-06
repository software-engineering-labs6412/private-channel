package org.ssau.privatechannel.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ssau.privatechannel.constants.Parameters;
import org.ssau.privatechannel.model.ConfidentialInfo;
import org.ssau.privatechannel.repository.ConfidentialInfoRepository;

import java.util.List;
import java.util.Objects;
import java.util.Random;

@Service
public class ConfidentialInfoService {

    private final ConfidentialInfoRepository infoRepository;

    private static final Random RANDOMIZER = new Random();

    @Autowired
    public ConfidentialInfoService(ConfidentialInfoRepository infoRepository) {
        this.infoRepository = infoRepository;
    }

    public List<ConfidentialInfo> findAllByIds(List<Long> ids) {
        return infoRepository.findAllByIds(ids);
    }

    public List<ConfidentialInfo> nextBatch() {
        return infoRepository.nextBatch();
    }

    public void deleteBatch(List<ConfidentialInfo> batch) {
        infoRepository.deleteBatch(batch);
    }

    public void addAll(List<ConfidentialInfo> info) {
        for (ConfidentialInfo confidentialInfo : info) {
            if (Objects.isNull(confidentialInfo.getId())) {
                confidentialInfo.setId(Math.abs(RANDOMIZER.nextLong()) % Parameters.MAX_ID);
            }
        }
        infoRepository.addAll(info);
    }

    public int getInfoCount() {
        return infoRepository.getInfoCount();
    }
}
