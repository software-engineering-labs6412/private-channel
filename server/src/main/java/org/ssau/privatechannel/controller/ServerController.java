package org.ssau.privatechannel.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ssau.privatechannel.exception.BadRequestException;
import org.ssau.privatechannel.exception.InternalServerErrorException;
import org.ssau.privatechannel.exception.NotFoundException;
import org.ssau.privatechannel.model.ConfidentialInfo;
import org.ssau.privatechannel.service.DataManagementService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = ServerController.Endpoints.API_V1)
public class ServerController {

    public static abstract class Endpoints {
        public static final String API_V1 = "/api/v1/server";
        private static final String UPLOAD_DATA = "/upload-data";
    }

    private final DataManagementService dataManagementService;

    @Autowired
    public ServerController(DataManagementService dataManagementService) {
        this.dataManagementService = dataManagementService;
    }

    @PostMapping(value = Endpoints.UPLOAD_DATA, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> uploadData(@RequestBody List<ConfidentialInfo> confidentialInfo) {
        try {
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

}
