package org.ssau.privatechannel.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ssau.privatechannel.model.Schedule;
import org.ssau.privatechannel.repository.ScheduleRepository;

@RestController
@RequestMapping(path = "/schedule")
public class ScheduleController {

    @Autowired
    ScheduleRepository scheduleRepository;

    private final String endPoint = "/api/v1";

    @PostMapping(value = endPoint, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void saveSchedule(@RequestBody Schedule schedule){
        scheduleRepository.add(schedule);
    }



}