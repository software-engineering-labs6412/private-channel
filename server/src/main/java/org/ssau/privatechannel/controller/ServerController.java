package org.ssau.privatechannel.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.ssau.privatechannel.constants.Endpoints;
import org.ssau.privatechannel.constants.SystemProperties;
import org.ssau.privatechannel.exception.BadRequestException;
import org.ssau.privatechannel.exception.HeaderKeyNotActualException;
import org.ssau.privatechannel.exception.InternalServerErrorException;
import org.ssau.privatechannel.exception.NotFoundException;
import org.ssau.privatechannel.model.ConfidentialInfo;
import org.ssau.privatechannel.service.AuthKeyService;
import org.ssau.privatechannel.service.DataManagementService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = Endpoints.API_V1_SERVER)
public class ServerController {

    private final DataManagementService dataManagementService;
    private final AuthKeyService authKeyService;

    @Autowired
    public ServerController(DataManagementService dataManagementService,
                            AuthKeyService authKeyService) {
        this.dataManagementService = dataManagementService;
        this.authKeyService = authKeyService;
    }

    @PostMapping(value = Endpoints.UPLOAD_DATA, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> uploadData(@RequestHeader(SystemProperties.HEADER_KEY) String headerKey,
                                        @RequestBody List<ConfidentialInfo> confidentialInfo) {
        try {
            try {
                checkHeaderKey(headerKey);
            }
            catch (HeaderKeyNotActualException e) {
                return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
            }
            dataManagementService.tryToSendDataToReceiver(confidentialInfo);
        } catch (BadRequestException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (InternalServerErrorException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
