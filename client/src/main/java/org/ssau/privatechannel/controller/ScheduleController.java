package org.ssau.privatechannel.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ssau.privatechannel.constants.SystemProperties;
import org.ssau.privatechannel.firetasks.EndDataTransferringTask;
import org.ssau.privatechannel.firetasks.StartDataTransferringTask;
import org.ssau.privatechannel.model.Schedule;
import org.ssau.privatechannel.model.TimeFrame;
import org.ssau.privatechannel.repository.ScheduleRepository;
import org.ssau.privatechannel.service.TimerService;

import java.time.LocalDateTime;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping(path = ScheduleController.Endpoints.API_V1)
public class ScheduleController {

    public static abstract class Endpoints {
        public static final String API_V1 = "/api/v1/client";
        public static final String SCHEDULE = "/schedule";
    }

    private final ScheduleRepository scheduleRepository;
    private final TimerService timerService;
    private final StartDataTransferringTask startDataTransferringTask;
    private final EndDataTransferringTask endDataTransferringTask;

    @Autowired
    public ScheduleController(ScheduleRepository scheduleRepository, TimerService timerService,
                       StartDataTransferringTask startDataTransferringTask,
                       EndDataTransferringTask endDataTransferringTask) {
        this.scheduleRepository = scheduleRepository;
        this.timerService = timerService;
        this.startDataTransferringTask = startDataTransferringTask;
        this.endDataTransferringTask = endDataTransferringTask;
    }

    @PostMapping(value = Endpoints.SCHEDULE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void saveSchedule(@RequestBody Schedule schedule) {

        for (int i = 0; i < schedule.getTimeFrames().size(); ++i) {
            TimeFrame timeFrame = schedule.getTimeFrames().get(i);
            timeFrame.setSchedule(schedule);
            schedule.getTimeFrames().set(i, timeFrame);
        }

        scheduleRepository.add(schedule);

        Collection<TimeFrame> timeFrames = schedule.getTimeFrames();
        for(TimeFrame timeFrame : timeFrames){
            LocalDateTime startTime = timeFrame.getStartTime();
            LocalDateTime endTime = timeFrame.getEndTime();

            String currentIp = System.getProperty(SystemProperties.CURRENT_IP);
            String receiverIp = System.getProperty(SystemProperties.RECEIVER_IP);

            startDataTransferringTask.setReceiverIp(receiverIp);

            timerService.createTask(startDataTransferringTask, startTime);
            log.info("Transferring data from client [IP={}] to client [IP={}] will be started at {}",
                    currentIp, receiverIp, startTime);
            timerService.createTask(endDataTransferringTask, endTime);
            log.info("Transferring data from client [IP={}] to client [IP={}] will be end at {}",
                    currentIp, receiverIp, endTime);
        }
    }

}