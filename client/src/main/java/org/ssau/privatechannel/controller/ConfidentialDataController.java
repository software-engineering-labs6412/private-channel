package org.ssau.privatechannel.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ssau.privatechannel.constants.Endpoints;
import org.ssau.privatechannel.model.ReceivedInformation;
import org.ssau.privatechannel.service.ReceivedInfoService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = Endpoints.API_V1_CLIENT)
public class ConfidentialDataController {

    private final ReceivedInfoService receivedInfoService;

    @Autowired
    public ConfidentialDataController(ReceivedInfoService receivedInfoService) {
        this.receivedInfoService = receivedInfoService;
    }

    @PostMapping(value = Endpoints.UPLOAD_DATA, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void uploadData(@RequestBody List<ReceivedInformation> confidentialInfo) {
        log.info("Received data [from IP={}]: {}",
                confidentialInfo.get(0).getSenderIP(), confidentialInfo);
        receivedInfoService.addAll(confidentialInfo);
    }

}
