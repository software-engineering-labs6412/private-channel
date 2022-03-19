package org.ssau.privatechannel.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ssau.privatechannel.comparator.TimeFrameComparatorToMax;
import org.ssau.privatechannel.comparator.TimeFrameComparatorToMin;
import org.ssau.privatechannel.firetasks.EndDataTransferringTask;
import org.ssau.privatechannel.firetasks.StartDataTransferringTask;
import org.ssau.privatechannel.model.Schedule;
import org.ssau.privatechannel.model.TimeFrame;
import org.ssau.privatechannel.repository.ScheduleRepository;
import org.ssau.privatechannel.service.ConfidentialInfoService;
import org.ssau.privatechannel.service.TimerService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping(path = "/api/v1")
public class ScheduleController {

    private final ScheduleRepository scheduleRepository;
    private final TimerService timerService;
    private final StartDataTransferringTask startDataTransferringTask;
    private final EndDataTransferringTask endDataTransferringTask;

    @Autowired
    ScheduleController(ScheduleRepository scheduleRepository, TimerService timerService,
                       StartDataTransferringTask startDataTransferringTask,
                       EndDataTransferringTask endDataTransferringTask) {
        this.scheduleRepository = scheduleRepository;
        this.timerService = timerService;
        this.startDataTransferringTask = startDataTransferringTask;
        this.endDataTransferringTask = endDataTransferringTask;
    }

    private final String END_POINT = "/schedule";

    @PostMapping(value = END_POINT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void saveSchedule(@RequestBody Schedule schedule) {
        scheduleRepository.add(schedule);

        Collection<TimeFrame> timeFrames = schedule.getTimeFrames();
        for(TimeFrame timeFrame : timeFrames){
            LocalDateTime startTime = timeFrame.getStartTime();
            LocalDateTime endTime = timeFrame.getEndTime();
            timerService.createTask(startDataTransferringTask, startTime);
            timerService.createTask(endDataTransferringTask, endTime);
        }
    }

}