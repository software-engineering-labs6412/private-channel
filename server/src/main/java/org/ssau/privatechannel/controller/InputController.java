package org.ssau.privatechannel.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.ssau.privatechannel.constants.Endpoints;
import org.ssau.privatechannel.constants.UrlSchemas;
import org.ssau.privatechannel.model.Schedule;
import org.ssau.privatechannel.service.ScheduleService;
import org.ssau.privatechannel.utils.ClientsHolder;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(Endpoints.API_V1_SERVER)
public class InputController {

    final private RestTemplate restTemplate;

    final private ScheduleService scheduleService;

    @Autowired
    public InputController(RestTemplate restTemplate, ScheduleService scheduleService) {
        this.restTemplate = restTemplate;
        this.scheduleService = scheduleService;
    }

    @PostMapping(value = Endpoints.SCHEDULES)
    public ResponseEntity<?> getSchedule(@RequestBody List<Schedule> schedules) {
        HttpEntity<Schedule> scheduleHttpEntity = new HttpEntity<>(schedules.get(0));

        List<String> allClients = ClientsHolder.getAllClients();

        for (String currentClient : allClients) {
            String fullUrl = UrlSchemas.HTTP + currentClient + Endpoints.API_V1_CLIENT + Endpoints.SCHEDULE;
            ResponseEntity<String> stringResponseEntity =
                    restTemplate.postForEntity(fullUrl, scheduleHttpEntity, String.class);
            boolean isStatusSuccessful = stringResponseEntity.getStatusCode().is2xxSuccessful();
            if (!isStatusSuccessful) {
                log.error("Cannot send first schedule to client by ip: {}. Client returned {}",
                        currentClient, stringResponseEntity.getStatusCode());
                if (stringResponseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST)) //400
                    return new ResponseEntity<>(
                            "The server cannot process the request sent by the client", HttpStatus.BAD_REQUEST);
                if (stringResponseEntity.getStatusCode().equals(HttpStatus.NOT_FOUND)) //404
                    return new ResponseEntity<>(
                            "The server did not find the page to which the link leads", HttpStatus.NOT_FOUND);
                if (stringResponseEntity.getStatusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR)) //500
                    return new ResponseEntity<>(
                            "Server configuration failure", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        for (int i = 1; i < schedules.size(); i++) {
            scheduleService.add(schedules.get(i));
        }

        return new ResponseEntity<>("Success", HttpStatus.OK);
    }


}
