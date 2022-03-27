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
import org.ssau.privatechannel.constants.Endpoints;
import org.ssau.privatechannel.model.Schedule;
import org.ssau.privatechannel.service.ScheduleService;

import java.util.Objects;

@Slf4j
@RestController
@RequestMapping(path = Endpoints.API_V1_SERVER)
public class ScheduleController {

    private final ScheduleService scheduleService;

    @Autowired
    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @PostMapping(value = Endpoints.GET_NEW_SCHEDULE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> sendSchedule(@RequestBody String requesterIp) {

        log.info("Searching schedule for client [ip={}]", requesterIp);

        Schedule schedule = scheduleService.findNextByIp(requesterIp);

        if (Objects.isNull(schedule)) {
            log.info("Schedule for client [ip={}] not found", requesterIp);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        log.info("Schedule for client [ip={}] found: {}", requesterIp, schedule);
        scheduleService.delete(schedule);
        return new ResponseEntity<>(schedule, HttpStatus.OK);
    }

}
