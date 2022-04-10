package org.ssau.privatechannel.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.ssau.privatechannel.model.ConfidentialInfo;
import org.ssau.privatechannel.service.AuthKeyService;
import org.ssau.privatechannel.service.DataManagementService;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ServerControllerTest {

    private final DataManagementService dataManagementService = Mockito.mock(DataManagementService.class);
    private final AuthKeyService authKeyService = Mockito.mock(AuthKeyService.class);

    private final Random RANDOMIZER = new Random();

    private static final String TEST_HEADER_KEY = "test_header_key";
    private static final String TEST_WRONG_KEY = "test_wrong_key";
    private static final int TEST_DATA_COUNT = 10;

    private final ServerController serverController = new ServerController(
            dataManagementService,
            authKeyService
    );

    @BeforeEach
    void setUp() {
        Mockito.when(authKeyService.isActual(Mockito.anyString())).thenReturn(false);
        Mockito.when(authKeyService.isActual(TEST_HEADER_KEY)).thenReturn(true);
    }

    @Test
    void uploadData() {
        ResponseEntity<?> response = serverController.uploadData(TEST_HEADER_KEY, generate(TEST_DATA_COUNT));
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK,
                "Expected: " + HttpStatus.OK + ", actual: " + response.getStatusCode());
    }

    @Test
    void uploadData_wrong() {
        ResponseEntity<?> response = serverController.uploadData(TEST_WRONG_KEY, generate(TEST_DATA_COUNT));
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.EXPECTATION_FAILED,
                "Expected: " + HttpStatus.EXPECTATION_FAILED + ", actual: " + response.getStatusCode());
    }

    private List<ConfidentialInfo> generate(int count) {
        List<ConfidentialInfo> info = new ArrayList<>();
        for (int i = 0; i < count; ++i) {
            ConfidentialInfo currentRecord = ConfidentialInfo.builder()
                    .id(null)
                    .data(generateRandomData())
                    .build();
            info.add(currentRecord);
        }
        return info;
    }

    private Map<String, Object> generateRandomData() {
        int MAX_DATA_ROWS_COUNT = 10;
        int rowsCount = 1 + (Math.abs(RANDOMIZER.nextInt()) % (MAX_DATA_ROWS_COUNT));

        Map<String, Object> result = new HashMap<>();
        for (int i = 0; i < rowsCount; ++i) {
            result.put(UUID.randomUUID().toString(), UUID.randomUUID() + UUID.randomUUID().toString());
        }
        return result;
    }
}