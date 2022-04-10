package org.ssau.privatechannel.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.ssau.privatechannel.constants.Endpoints;
import org.ssau.privatechannel.constants.SystemProperties;
import org.ssau.privatechannel.exception.HeaderKeyNotActualException;
import org.ssau.privatechannel.model.ReceivedInformation;
import org.ssau.privatechannel.service.AuthKeyService;
import org.ssau.privatechannel.service.ReceivedInfoService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = Endpoints.API_V1_CLIENT)
public class ConfidentialDataController {

    private final ReceivedInfoService receivedInfoService;
    private final AuthKeyService authKeyService;

    @Autowired
    public ConfidentialDataController(ReceivedInfoService receivedInfoService,
                                      AuthKeyService authKeyService) {
        this.receivedInfoService = receivedInfoService;
        this.authKeyService = authKeyService;
    }

    @PostMapping(value = Endpoints.UPLOAD_DATA, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> uploadData(@RequestHeader(SystemProperties.HEADER_KEY) String headerKey,
                                        @RequestBody List<ReceivedInformation> confidentialInfo) {
        try {
            checkHeaderKey(headerKey);
        }
        catch (HeaderKeyNotActualException e) {
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }

        log.info("Received data [from IP={}]: {}",
                confidentialInfo.get(0).getSenderIP(), confidentialInfo);
        receivedInfoService.addAll(confidentialInfo);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private void checkHeaderKey(String key) throws HeaderKeyNotActualException {
        if (!authKeyService.isActual(key)) {
            String errorMessage = String.format("Key %s not actual and not persisted in database", key);
            log.error(errorMessage);
            throw new HeaderKeyNotActualException(errorMessage);
        }
    }

}
