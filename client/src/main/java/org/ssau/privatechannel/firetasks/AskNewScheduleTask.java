package org.ssau.privatechannel.firetasks;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.ssau.privatechannel.constants.Endpoints;
import org.ssau.privatechannel.constants.SystemProperties;
import org.ssau.privatechannel.constants.UrlSchemas;
import org.ssau.privatechannel.model.Schedule;
import org.ssau.privatechannel.service.IpService;
import org.ssau.privatechannel.service.NetworkAdapterService;
import org.ssau.privatechannel.service.ScheduleService;
import org.ssau.privatechannel.utils.SystemContext;

import java.util.Objects;
import java.util.TimerTask;

@Slf4j
public class AskNewScheduleTask extends TimerTask  {

    private final RestTemplate restTemplate;
    private final IpService ipService;
    private final NetworkAdapterService networkAdapterService;
    private final ScheduleService scheduleService;

    public AskNewScheduleTask(RestTemplate restTemplate,
                              IpService ipService,
                              NetworkAdapterService networkAdapterService,
                              ScheduleService scheduleService) {
        this.restTemplate = restTemplate;
        this.ipService = ipService;
        this.networkAdapterService = networkAdapterService;
        this.scheduleService = scheduleService;
    }

    @Override
    public void run() {
        String serverIp = SystemContext.getProperty(SystemProperties.SERVER_IP);
        String urlRequestSchedule = UrlSchemas.HTTP +
                serverIp +
                Endpoints.API_V1_SERVER +
                Endpoints.GET_NEW_SCHEDULE;

        String currentIp = SystemContext.getProperty(SystemProperties.CURRENT_IP);
        HttpEntity<String> httpEntity = new HttpEntity<>(currentIp);

        log.info("Requesting new schedule from server [url = {}]", urlRequestSchedule);
        ResponseEntity<Schedule> scheduleResponseEntity =
                restTemplate.postForEntity(urlRequestSchedule, httpEntity, Schedule.class);

        if (!scheduleResponseEntity.getStatusCode().is2xxSuccessful()) {
            log.error("Could not get schedule from server. Server returned {}",
                    scheduleResponseEntity.getStatusCode());
            System.exit(0); // TODO: dmso добавить попытки взять расписание
        }

        String urlProvideScheduleToClient = UrlSchemas.HTTP +
                currentIp +
                Endpoints.API_V1_CLIENT +
                Endpoints.SCHEDULE;

        if (Objects.isNull(scheduleResponseEntity.getBody())) {
            log.info("Schedule does not exist right now. Exiting program...");
            System.exit(0);
        }
        log.info("New schedule got: {}", scheduleResponseEntity.getBody());

        HttpEntity<?> scheduleEntity = new HttpEntity<>(scheduleResponseEntity.getBody());

        ResponseEntity<String> response =
                restTemplate.postForEntity(urlProvideScheduleToClient, scheduleEntity, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("Could not send new schedule to client. Client returned {}",
                    scheduleResponseEntity.getStatusCode());
            System.exit(0); // TODO: dmso добавить попытки
        }

        EndDataTransferringTask endDataTransferringTask = new EndDataTransferringTask(
                ipService, networkAdapterService, scheduleService, scheduleResponseEntity.getBody());
        endDataTransferringTask.run();
    }
}
