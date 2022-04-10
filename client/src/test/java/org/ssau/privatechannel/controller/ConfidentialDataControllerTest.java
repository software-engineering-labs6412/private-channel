package org.ssau.privatechannel.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.ssau.privatechannel.model.ReceivedInformation;
import org.ssau.privatechannel.service.AuthKeyService;
import org.ssau.privatechannel.service.ConfidentialInfoService;
import org.ssau.privatechannel.service.ReceivedInfoService;
import org.ssau.privatechannel.utils.RandomDataGenerator;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ConfidentialDataControllerTest {

    private final ReceivedInfoService receivedInfoService = Mockito.mock(ReceivedInfoService.class);
    private final AuthKeyService authKeyService = Mockito.mock(AuthKeyService.class);
    private final RandomDataGenerator dataGenerator = new RandomDataGenerator();

    private final ConfidentialDataController dataController
            = new ConfidentialDataController(receivedInfoService,
            authKeyService);

    private static final String TEST_HEADER_KEY = "test_header_key";
    private static final String TEST_WRONG_KEY = "test_wrong_key";
    private static final int TEST_DATA_COUNT = 10;

    @BeforeEach
    void setUp() {
        Mockito.when(authKeyService.isActual(Mockito.anyString())).thenReturn(false);
        Mockito.when(authKeyService.isActual(TEST_HEADER_KEY)).thenReturn(true);
    }

    @Test
    void uploadData() {
        List<ReceivedInformation> info = dataGenerator.generateReceivedData(TEST_DATA_COUNT);
        ResponseEntity<?> responseEntity = dataController.uploadData(TEST_HEADER_KEY, info);
        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.OK,
                "Expected: " + HttpStatus.OK + ", actual: " + responseEntity.getStatusCode());
    }

    @Test
    void uploadData_WrongKey() {
        List<ReceivedInformation> info = dataGenerator.generateReceivedData(TEST_DATA_COUNT);
        ResponseEntity<?> responseEntity = dataController.uploadData(TEST_WRONG_KEY, info);
        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.EXPECTATION_FAILED,
                "Expected: " + HttpStatus.EXPECTATION_FAILED + ", actual: " + responseEntity.getStatusCode());
    }



}