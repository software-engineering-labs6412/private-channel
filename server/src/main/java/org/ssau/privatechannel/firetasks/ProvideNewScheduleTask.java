package org.ssau.privatechannel.firetasks;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.ssau.privatechannel.constants.Endpoints;
import org.ssau.privatechannel.model.Schedule;
import org.ssau.privatechannel.service.ScheduleService;
import org.ssau.privatechannel.utils.Base64;
import org.ssau.privatechannel.utils.ClientsHolder;

import java.util.*;

@Slf4j
@Component
@ComponentScan("org.ssau.privatechannel.config")
public class ProvideNewScheduleTask extends TimerTask {

    private final ScheduleService scheduleService;
    private final RestTemplate restTemplate;

    @Autowired
    public ProvideNewScheduleTask(ScheduleService scheduleService,
                                  RestTemplate restTemplate) {
        this.scheduleService = scheduleService;
        this.restTemplate = restTemplate;
    }

    @Override
    public void run() {
        List<Schedule> allSchedules = new ArrayList<>(scheduleService.findAll());
        Optional<Schedule> optionalSchedule = allSchedules.stream().findFirst();
        if (optionalSchedule.isEmpty()) {
            return;
        }

        Schedule schedule = optionalSchedule.get();

        String encodedKey = Base64.encode(UUID.randomUUID().toString());

        HttpHeaders headers = new HttpHeaders();
        headers.add(Headers.KEY, encodedKey);

        HttpEntity<Schedule> entity = new HttpEntity<>(schedule, headers);

        List<String> allClients = ClientsHolder.getAllClients();

        for (String currentIp : allClients) {
            String clientUrl = "http://" + currentIp + Endpoints.API_V1_CLIENT + Endpoints.SCHEDULE;
            ResponseEntity<String> response = restTemplate.postForEntity(clientUrl, entity, String.class);
            if (response.getStatusCode() != HttpStatus.OK) {
                String errorMessage = String.format(
                        "Could not provide schedule to client with IP = %s [url = %s]", currentIp, clientUrl);
                log.error(errorMessage);
                throw new RuntimeException(errorMessage);
            }
        }

        scheduleService.delete(schedule);
    }

    private static abstract class Headers {
        public static final String KEY = "X-Request-Key";
    }
}
