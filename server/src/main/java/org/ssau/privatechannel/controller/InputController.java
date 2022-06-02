package org.ssau.privatechannel.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.ssau.privatechannel.constants.Endpoints;
import org.ssau.privatechannel.constants.Parameters;
import org.ssau.privatechannel.constants.UrlSchemas;
import org.ssau.privatechannel.exception.ValidationException;
import org.ssau.privatechannel.model.Schedule;
import org.ssau.privatechannel.model.TimeFrame;
import org.ssau.privatechannel.service.AuthKeyService;
import org.ssau.privatechannel.service.ScheduleService;
import org.ssau.privatechannel.service.ScheduleValidatorService;
import org.ssau.privatechannel.utils.ClientsHolder;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@RestController
@RequestMapping(Endpoints.API_V1_SERVER)
public class InputController {

    private final RestTemplate restTemplate;
    private final ScheduleService scheduleService;
    private final AuthKeyService authKeyService;
    private final ScheduleValidatorService validatorService;

    private static final Random RANDOMIZER = new Random();

    @Autowired
    public InputController(RestTemplate restTemplate,
                           ScheduleService scheduleService,
                           AuthKeyService authKeyService,
                           ScheduleValidatorService validatorService) {
        this.restTemplate = restTemplate;
        this.scheduleService = scheduleService;
        this.authKeyService = authKeyService;
        this.validatorService = validatorService;
    }

    @PostMapping(value = Endpoints.SCHEDULES)
    public ResponseEntity<?> provideSchedules(@RequestBody List<Schedule> schedules) {

        authKeyService.generateNewHeaderKey();

        String clientId = schedules.get(0).getClientIp();
        if (Objects.nonNull(scheduleService.findNextByIp(clientId))) {
            scheduleService.addAll(schedules);
            String logMessage = String.format("Schedules for client [IP = %s] already exists. " +
                            "Schedules will be added in queue", clientId);
            log.info(logMessage);
            return new ResponseEntity<>(logMessage, HttpStatus.ACCEPTED);
        }

        provideIds(schedules);

        try {
            validatorService.validateSchedules(schedules);
        } catch (ValidationException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        List<String> allClients = ClientsHolder.getAllClients();

        for (String currentClient : allClients) {

            List<Schedule> allSchedulesForCurrentUser = getSchedulesForClient(schedules, currentClient);
            Schedule firstSchedule = allSchedulesForCurrentUser.get(0);
            HttpEntity<Schedule> scheduleHttpEntity = new HttpEntity<>(firstSchedule);

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

            allSchedulesForCurrentUser.remove(0);
            scheduleService.addAll(allSchedulesForCurrentUser);
        }

        return new ResponseEntity<>("Success", HttpStatus.OK);
    }

    @PostMapping(value = Endpoints.GENERATE_SCHEDULE, consumes = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<?> generateSchedule(@PathVariable("duration") Integer duration,
                                              @RequestBody String clientIp) {

        int startDelay = 20;
        int delayBetweenSchedules = 60;

        List<Schedule> schedules = new ArrayList<>();
        Schedule schedule = new Schedule();

        TimeFrame timeFrame = new TimeFrame();
        timeFrame.setStartTime(LocalDateTime.now().plusSeconds(startDelay));
        timeFrame.setEndTime(LocalDateTime.now().plusSeconds(startDelay + duration));

        TimeFrame timeFrame2 = new TimeFrame();
        timeFrame2.setStartTime(timeFrame.getEndTime().plusSeconds(delayBetweenSchedules));
        timeFrame2.setEndTime(timeFrame.getEndTime().plusSeconds(delayBetweenSchedules + duration));

        List<TimeFrame> timeFrames = Arrays.asList(timeFrame, timeFrame2);
        schedule.setTimeFrames(timeFrames);
        schedules.add(schedule);
        schedule.setClientIp(clientIp);

        return provideSchedules(schedules);
    }

    private void provideIds(List<Schedule> schedules) {
        for (Schedule schedule : schedules) {
            if (Objects.isNull(schedule.getId()))
                schedule.setId(Math.abs(RANDOMIZER.nextLong()) % Parameters.MAX_ID);
            for (TimeFrame timeFrame : schedule.getTimeFrames()) {
                if (Objects.isNull(timeFrame.getId())) {
                    timeFrame.setId(Math.abs(RANDOMIZER.nextLong()) % Parameters.MAX_ID);
                }
            }
        }
    }

    private List<Schedule> getSchedulesForClient(List<Schedule> schedules, String clientIp) {
        List<Schedule> result = new ArrayList<>();
        clientIp = clientIp.split(":")[0];
        for (Schedule schedule : schedules) {
            if (schedule.getClientIp().equals(clientIp)) {
                result.add(schedule);
            }
        }
        return result;
    }
}
