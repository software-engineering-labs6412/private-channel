package org.ssau.privatechannel.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ssau.privatechannel.constants.Endpoints;
import org.ssau.privatechannel.model.Schedule;
import org.ssau.privatechannel.model.TimeFrame;
import org.ssau.privatechannel.repository.ScheduleRepository;
import org.ssau.privatechannel.service.TasksService;

@Slf4j
@RestController
@RequestMapping(path = Endpoints.API_V1_CLIENT)
public class ScheduleController {

    private final ScheduleRepository scheduleRepository;
    private final TasksService tasksService;

    @Autowired
    public ScheduleController(ScheduleRepository scheduleRepository,
                              TasksService tasksService) {
        this.scheduleRepository = scheduleRepository;
        this.tasksService = tasksService;
    }

    @PostMapping(value = Endpoints.SCHEDULE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void saveSchedule(@RequestBody Schedule schedule) {

        for (int i = 0; i < schedule.getTimeFrames().size(); ++i) {
            TimeFrame timeFrame = schedule.getTimeFrames().get(i);
            schedule.getTimeFrames().set(i, timeFrame);
        }

        scheduleRepository.add(schedule);
        tasksService.plan(schedule);
    }
}