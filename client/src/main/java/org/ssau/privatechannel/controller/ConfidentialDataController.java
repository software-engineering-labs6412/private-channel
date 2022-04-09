package org.ssau.privatechannel.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.ssau.privatechannel.constants.Endpoints;
import org.ssau.privatechannel.model.ConfidentialInfo;
import org.ssau.privatechannel.model.ReceivedInformation;
import org.ssau.privatechannel.service.ConfidentialInfoService;
import org.ssau.privatechannel.service.ReceivedInfoService;
import org.ssau.privatechannel.utils.RandomDataGenerator;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = Endpoints.API_V1_CLIENT)
public class ConfidentialDataController {

    private final ReceivedInfoService receivedInfoService;
    private final ConfidentialInfoService infoService;
    private final RandomDataGenerator dataGenerator;

    @Autowired
    public ConfidentialDataController(ReceivedInfoService receivedInfoService,
                                      RandomDataGenerator dataGenerator,
                                      ConfidentialInfoService infoService) {
        this.receivedInfoService = receivedInfoService;
        this.dataGenerator = dataGenerator;
        this.infoService = infoService;
    }

    @PostMapping(value = Endpoints.UPLOAD_DATA, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void uploadData(@RequestBody List<ReceivedInformation> confidentialInfo) {
        log.info("Received data [from IP={}]: {}",
                confidentialInfo.get(0).getSenderIP(), confidentialInfo);
        receivedInfoService.addAll(confidentialInfo);
    }

    @PostMapping(value = Endpoints.GENERATE_DATA)
    public void generate(@PathVariable("count") Integer count) {
        log.debug("Generate test data...");
        List<ConfidentialInfo> data = dataGenerator.generate(count);
        infoService.addAll(data);
    }

}
