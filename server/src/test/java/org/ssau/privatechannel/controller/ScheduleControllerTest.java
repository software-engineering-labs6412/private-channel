package org.ssau.privatechannel.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.ssau.privatechannel.model.Schedule;
import org.ssau.privatechannel.model.TimeFrame;
import org.ssau.privatechannel.service.ScheduleService;

import java.time.LocalDateTime;
import java.util.List;

class ScheduleControllerTest {

    private final ScheduleService scheduleService = Mockito.mock(ScheduleService.class);

    private final ScheduleController scheduleController = new ScheduleController(scheduleService);

    private static final String TEST_REQUESTER_IP = "10.20.30.40";

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
        Mockito.when(scheduleService.findNextByIp(TEST_REQUESTER_IP)).thenReturn(TEST_SCHEDULE);
        Mockito.when(scheduleService.isActualSchedule(TEST_SCHEDULE)).thenReturn(true);
    }

    @Test
    void sendSchedule() {
        ResponseEntity<?> response = scheduleController.sendSchedule(TEST_REQUESTER_IP);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode(),
                "Expected: " + HttpStatus.OK + ", actual: " + response.getStatusCode());
    }
}