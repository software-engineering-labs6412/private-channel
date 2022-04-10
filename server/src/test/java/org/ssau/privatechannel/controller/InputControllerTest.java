package org.ssau.privatechannel.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.ssau.privatechannel.model.Schedule;
import org.ssau.privatechannel.model.TimeFrame;
import org.ssau.privatechannel.service.AuthKeyService;
import org.ssau.privatechannel.service.ScheduleService;
import org.ssau.privatechannel.service.ScheduleValidatorService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

class InputControllerTest {

    private final RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
    private final ScheduleService scheduleService = Mockito.mock(ScheduleService.class);
    private final AuthKeyService authKeyService = Mockito.mock(AuthKeyService.class);
    private final ScheduleValidatorService validatorService = Mockito.mock(ScheduleValidatorService.class);

    private final InputController inputController = new InputController(
            restTemplate,
            scheduleService,
            authKeyService,
            validatorService
    );

    private static final Long TEST_SCHEDULE_ID = 10L;
    private static final Long TEST_TIMEFRAME_ID = 20L;
    private static final String TEST_CLIENT_IP = "10.20.30.40";
    private static final List<TimeFrame> TEST_TIMEFRAMES =
            List.of(
                    TimeFrame.builder()
                            .id(TEST_TIMEFRAME_ID)
                            .startTime(LocalDateTime.now())
                            .endTime(LocalDateTime.now().plusSeconds(1))
                            .build()
            );

    private static final Schedule TEST_SCHEDULE = Schedule.builder()
            .id(TEST_SCHEDULE_ID)
            .clientIp(TEST_CLIENT_IP)
            .timeFrames(TEST_TIMEFRAMES)
            .build();

    private static final List<Schedule> TEST_SCHEDULES = new ArrayList<>();
    static {
        TEST_SCHEDULES.add(TEST_SCHEDULE);
    }

    @Test
    void provideSchedules() {
        ResponseEntity<?> response = inputController.provideSchedules(TEST_SCHEDULES);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode(),
                "Expected: " + HttpStatus.OK + ", actual: " + response.getStatusCode());
    }
}