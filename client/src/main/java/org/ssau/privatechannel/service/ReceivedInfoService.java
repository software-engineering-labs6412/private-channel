package org.ssau.privatechannel.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ssau.privatechannel.model.ReceivedInformation;
import org.ssau.privatechannel.repository.ReceivedInfoRepository;

import java.util.List;

@Service
public class ReceivedInfoService {

    private final ReceivedInfoRepository infoRepository;

    @Autowired
    public ReceivedInfoService(ReceivedInfoRepository infoRepository) {
        this.infoRepository = infoRepository;
    }

    public void addAll(List<ReceivedInformation> info) {
        infoRepository.addAll(info);
    }
}
