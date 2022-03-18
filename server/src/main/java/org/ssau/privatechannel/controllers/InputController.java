package org.ssau.privatechannel.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.ssau.privatechannel.exception.ServiceException;
import org.ssau.privatechannel.model.Schedule;
import org.ssau.privatechannel.service.ScheduleService;

import java.util.List;

@RestController
public class InputController {

    private static class PathVariable {
        private static final String URL_SHEDULES = "/api/v1/server/schedules";
    }

    final private RestTemplate restTemplate;

    final private ScheduleService scheduleService;

    @Autowired
    public InputController(RestTemplate restTemplate, ScheduleService scheduleService) {
        this.restTemplate = restTemplate;
        this.scheduleService = scheduleService;
    }

    @PostMapping(value = PathVariable.URL_SHEDULES)
    public void getSchedule(@RequestBody List<Schedule> schedules) throws ServiceException {
        HttpEntity<Schedule> scheduleHttpEntity = new HttpEntity<>(schedules.get(0)); //по заданию первое расписание отправляем клиентам, остальные отправляем в бд
        ResponseEntity<String> stringResponseEntity = restTemplate.postForEntity("", scheduleHttpEntity, String.class);//TODO добавить EndPoint (URL)
        Boolean statusSuccessful = stringResponseEntity.getStatusCode().is2xxSuccessful();
        if (!statusSuccessful) {
            if (stringResponseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST)) //400
                throw new ServiceException("The server cannot process the request sent by the client");
            if (stringResponseEntity.getStatusCode().equals(HttpStatus.NOT_FOUND)) //404
                throw new ServiceException("The server did not find the page to which the link leads");
            if (stringResponseEntity.getStatusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR))//500
                throw new ServiceException("Server configuration failure");
        }
        for (int i = 1; i < schedules.size(); i++) {
            scheduleService.add(schedules.get(i));
        }


    }


}
