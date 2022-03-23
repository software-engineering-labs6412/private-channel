package org.ssau.privatechannel.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ssau.privatechannel.model.ReceivedInformation;
import org.ssau.privatechannel.service.ReceivedInfoService;

@RestController
@RequestMapping(value = ConfidentialDataController.Endpoints.API_V1)
public class ConfidentialDataController {

    public static abstract class Endpoints {
        public static final String API_V1 = "/api/v1";
        public static final String UPLOAD_DATA = "/upload-data";
    }

    private final ReceivedInfoService receivedInfoService;

    @Autowired
    public ConfidentialDataController(ReceivedInfoService receivedInfoService) {
        this.receivedInfoService = receivedInfoService;
    }

    @PostMapping(value = Endpoints.UPLOAD_DATA, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void uploadData(@RequestBody ReceivedInformation confidentialInfo) {
        receivedInfoService.add(confidentialInfo);
    }

}
