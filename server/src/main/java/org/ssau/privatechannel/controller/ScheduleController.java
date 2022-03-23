package org.ssau.privatechannel.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ssau.privatechannel.model.Schedule;
import org.ssau.privatechannel.repository.ScheduleRepository;

import java.util.LinkedList;

@RestController
@RequestMapping(path = "/schedule")
public class ScheduleController {

    private final String endPoint = "/api/v1";

    private final ScheduleRepository scheduleRepository;

    @Autowired
    public ScheduleController(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    @PostMapping(value = endPoint, produces = MediaType.APPLICATION_JSON_VALUE)
    public Schedule sendSchedule() {
        LinkedList<Schedule> list = new LinkedList<>(scheduleRepository.findAll());
        return list.remove(0);
    }

}
