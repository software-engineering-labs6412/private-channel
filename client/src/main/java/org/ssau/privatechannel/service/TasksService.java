package org.ssau.privatechannel.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.ssau.privatechannel.constants.SystemProperties;
import org.ssau.privatechannel.firetasks.AskNewScheduleTask;
import org.ssau.privatechannel.firetasks.EndDataTransferringTask;
import org.ssau.privatechannel.firetasks.StartDataTransferringTask;
import org.ssau.privatechannel.model.Schedule;
import org.ssau.privatechannel.model.TimeFrame;
import org.ssau.privatechannel.utils.SystemContext;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class TasksService {

    private static final String ZONE_ID_STR = "Europe/Samara";

    private final ConfidentialInfoService infoService;
    private final IpService ipService;
    private final NetworkAdapterService networkAdapterService;
    private final RestTemplate restTemplate;
    private final TimerService timerService;

    @Autowired
    public TasksService(ConfidentialInfoService infoService,
                        IpService ipService,
                        TimerService timerService,
                        NetworkAdapterService networkAdapterService,
                        RestTemplate restTemplate) {
        this.infoService = infoService;
        this.ipService = ipService;
        this.networkAdapterService = networkAdapterService;
        this.restTemplate = restTemplate;
        this.timerService = timerService;
    }

    public void plan(Schedule schedule) {
        Collection<TimeFrame> timeFrames = schedule.getTimeFrames();
        for (TimeFrame timeFrame : timeFrames) {
            LocalDateTime startTime = timeFrame.getStartTime();
            LocalDateTime endTime = timeFrame.getEndTime();

            String currentIp = SystemContext.getProperty(SystemProperties.CURRENT_IP);
            String receiverIp = SystemContext.getProperty(SystemProperties.RECEIVER_IP);

            StartDataTransferringTask startTransferringTask =
                    new StartDataTransferringTask(infoService,
                            restTemplate,
                            ipService,
                            networkAdapterService);
            startTransferringTask.setReceiverIp(receiverIp);

            timerService.createTask(startTransferringTask, startTime);
            log.info("Transferring data from client [IP={}] to client [IP={}] will be started at {}",
                    currentIp, receiverIp, startTime);

            if (isLastTimeframe(schedule, timeFrame)) {
                AskNewScheduleTask askNewScheduleTask = new AskNewScheduleTask(restTemplate,
                        ipService,
                        networkAdapterService);

                log.info("New schedule will be requested at {}", timeFrame.getEndTime());
                timerService.createTask(askNewScheduleTask, timeFrame.getEndTime());
                break;
            }

            EndDataTransferringTask endTransferringTask =
                    new EndDataTransferringTask(ipService, networkAdapterService);

            timerService.createTask(endTransferringTask, endTime);
            log.info("Transferring data from client [IP={}] to client [IP={}] will be end at {}",
                    currentIp, receiverIp, endTime);
        }
    }

    private boolean isLastTimeframe(Schedule schedule, TimeFrame timeFrame) {
        List<TimeFrame> timeFrames = schedule.getTimeFrames();
        return timeFrames.get(timeFrames.size()-1).equals(timeFrame);
    }
}
