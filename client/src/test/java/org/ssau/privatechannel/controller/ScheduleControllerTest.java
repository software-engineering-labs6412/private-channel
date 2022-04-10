package org.ssau.privatechannel.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.ssau.privatechannel.model.Schedule;
import org.ssau.privatechannel.model.TimeFrame;
import org.ssau.privatechannel.service.AuthKeyService;
import org.ssau.privatechannel.service.ScheduleService;
import org.ssau.privatechannel.service.TasksService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

class ScheduleControllerTest {

    private final ScheduleService scheduleService = Mockito.mock(ScheduleService.class);
    private final TasksService tasksService = Mockito.mock(TasksService.class);
    private final AuthKeyService authKeyService = Mockito.mock(AuthKeyService.class);

    private final ScheduleController scheduleController = new ScheduleController(
            scheduleService,
            tasksService,
            authKeyService);

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

    @BeforeEach
    void setUp() {
    }

    @Test
    void saveSchedule() {
        ResponseEntity<?> response = scheduleController.saveSchedule(TEST_SCHEDULE);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.ACCEPTED,
                "Expected: " + HttpStatus.ACCEPTED + ", actual: " + response.getStatusCode());
    }
}