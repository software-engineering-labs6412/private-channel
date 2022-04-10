package org.ssau.privatechannel.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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
import org.ssau.privatechannel.service.TasksService;
import org.ssau.privatechannel.utils.SystemContext;

import java.util.Objects;

@Slf4j
@RestController
@RequestMapping(path = Endpoints.API_V1_CLIENT)
public class ScheduleController {

    private final ScheduleRepository scheduleRepository;
    private final TasksService tasksService;
    private final AuthKeyService authKeyService;

    @Autowired
    public ScheduleController(ScheduleRepository scheduleRepository,
                              TasksService tasksService,
                              AuthKeyService authKeyService) {
        this.scheduleRepository = scheduleRepository;
        this.tasksService = tasksService;
        this.authKeyService = authKeyService;
    }

    @PostMapping(value = Endpoints.SCHEDULE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void saveSchedule(@RequestBody Schedule schedule) {

        generateNewHeaderKey();

        for (int i = 0; i < schedule.getTimeFrames().size(); ++i) {
            TimeFrame timeFrame = schedule.getTimeFrames().get(i);
            schedule.getTimeFrames().set(i, timeFrame);
        }

        scheduleRepository.add(schedule);
        tasksService.plan(schedule);
    }

    private void generateNewHeaderKey() {
        String headerKey = SystemContext.getProperty(SystemProperties.HEADER_KEY);

        AuthorizationKey previousKey = authKeyService.get();
        if (Objects.isNull(previousKey))
            log.warn("Auth service returned empty result. Key will be created");
        else
            authKeyService.delete(previousKey);

        AuthorizationKey newKey = new AuthorizationKey(null, headerKey);
        authKeyService.set(newKey);
    }
}