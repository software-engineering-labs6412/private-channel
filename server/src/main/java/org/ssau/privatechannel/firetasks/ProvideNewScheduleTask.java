package org.ssau.privatechannel.firetasks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.ssau.privatechannel.constants.SystemProperties;
import org.ssau.privatechannel.model.Schedule;
import org.ssau.privatechannel.service.ScheduleService;
import org.ssau.privatechannel.utils.Base64;

import java.util.*;

@Component
@ComponentScan("org.ssau.privatechannel.config")
public class ProvideNewScheduleTask extends TimerTask {

    private final ScheduleService scheduleService;
    private final RestTemplate restTemplate;

    private static final String SCHEDULE_ENDPOINT = "/api/v1/schedule";

    private static abstract class Headers {
        public static final String KEY = "X-Request-Key";
    }

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

        String clientUrl1 = "http://" + /*TODO: address*/ SCHEDULE_ENDPOINT;
        ResponseEntity<String> response = restTemplate.postForEntity(clientUrl1, entity, String.class);
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Could not provide schedule to client");
        }

        String clientUrl2 = "http://" + /*TODO: address*/ SCHEDULE_ENDPOINT;
        response = restTemplate.postForEntity(clientUrl2, entity, String.class);
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Could not provide schedule to client");
        }

        scheduleService.delete(schedule);
    }
}
