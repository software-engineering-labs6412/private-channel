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
import org.ssau.privatechannel.constants.SystemProperties;
import org.ssau.privatechannel.model.AuthorizationKey;
import org.ssau.privatechannel.model.Schedule;
import org.ssau.privatechannel.model.TimeFrame;
import org.ssau.privatechannel.repository.ScheduleRepository;
import org.ssau.privatechannel.service.AuthKeyService;
import org.ssau.privatechannel.service.ScheduleService;
import org.ssau.privatechannel.service.TasksService;
import org.ssau.privatechannel.utils.SystemContext;

import java.util.Objects;

@Slf4j
@RestController
@RequestMapping(path = Endpoints.API_V1_CLIENT)
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final TasksService tasksService;
    private final AuthKeyService authKeyService;

    @Autowired
    public ScheduleController(ScheduleService scheduleService,
                              TasksService tasksService,
                              AuthKeyService authKeyService) {
        this.scheduleService = scheduleService;
        this.tasksService = tasksService;
        this.authKeyService = authKeyService;
    }

    @PostMapping(value = Endpoints.SCHEDULE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> saveSchedule(@RequestBody Schedule schedule) {
        authKeyService.generateNewHeaderKey();
        scheduleService.add(schedule);
        tasksService.plan(schedule);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}