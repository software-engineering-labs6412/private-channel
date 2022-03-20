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
@RequestMapping(value = "/api/v1")
public class ConfidentionalDataController {

    private final String END_POINT = "/upload-data";

    private final ReceivedInfoService receivedInfoService;

    @Autowired
    ConfidentionalDataController(ReceivedInfoService receivedInfoService) {
        this.receivedInfoService = receivedInfoService;
    }

    @PostMapping(value = END_POINT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void uploadData(@RequestBody ReceivedInformation confidentialInfo) {
        receivedInfoService.add(confidentialInfo);
    }

}
