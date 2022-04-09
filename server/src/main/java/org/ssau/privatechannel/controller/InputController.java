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
import org.ssau.privatechannel.service.ScheduleService;
import org.ssau.privatechannel.utils.ClientsHolder;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@RestController
@RequestMapping(Endpoints.API_V1_SERVER)
public class InputController {

    private final RestTemplate restTemplate;
    private final ScheduleService scheduleService;

    private static final Random RANDOMIZER = new Random();

    @Autowired
    public InputController(RestTemplate restTemplate, ScheduleService scheduleService) {
        this.restTemplate = restTemplate;
        this.scheduleService = scheduleService;
    }

    @PostMapping(value = Endpoints.SCHEDULES)
    public ResponseEntity<?> provideSchedules(@RequestBody List<Schedule> schedules) {

        String clientId = schedules.get(0).getClientIp();
        if (Objects.nonNull(scheduleService.findNextByIp(clientId))) {
            scheduleService.addAll(schedules);
            String logMessage = String.format("Schedules for client [IP = %s] already exists. " +
                            "Schedules will be added in queue", clientId);
            log.info(logMessage);
            return new ResponseEntity<>(logMessage, HttpStatus.ACCEPTED);
        }

        for (Schedule schedule : schedules) {
            if (Objects.isNull(schedule.getId()))
                schedule.setId(Math.abs(RANDOMIZER.nextLong()) % Parameters.MAX_ID);
            for (TimeFrame timeFrame : schedule.getTimeFrames()) {
                if (Objects.isNull(timeFrame.getId())) {
                    timeFrame.setId(Math.abs(RANDOMIZER.nextLong()) % Parameters.MAX_ID);
                }
            }
        }

        try {
            validateSchedules(schedules);
        } catch (ValidationException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

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

        schedules.remove(0);
        scheduleService.addAll(schedules);

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

    private void validateSchedules(List<Schedule> schedules) throws ValidationException {
        for (Schedule schedule : schedules) {
            validateSchedule(schedule);
        }
    }

    private void validateSchedule(Schedule schedule) throws ValidationException {
        for (TimeFrame timeFrame : schedule.getTimeFrames()) {
            validateTimeframe(timeFrame);
        }

        checkTimeFramesForIntersects(schedule.getTimeFrames());
        checkTimeframesOrder(schedule.getTimeFrames());
    }

    private void validateTimeframe(TimeFrame timeFrame) throws ValidationException {

        if (timeFrame.getStartTime().isBefore(LocalDateTime.now())) {
            String errorMessage = String.format("Timeframe %s has start before current time. " +
                    "It is incorrect", timeFrame);
            log.error(errorMessage);
            throw new ValidationException(errorMessage);
        }

        if (timeFrame.getStartTime().isAfter(timeFrame.getEndTime())){
            String errorMessage = String.format(
                    "Schedule is incorrect. Timeframe start must be before timeframe end. Got: %s", timeFrame);
            log.error(errorMessage);
            throw new ValidationException(errorMessage);
        }
    }

    private void checkTimeFramesForIntersects(List<TimeFrame> timeFrames) throws ValidationException {
        for (int i = 0; i < timeFrames.size()-1; ++i) {
            for (int j = 1; j < timeFrames.size(); ++j) {
                if (timeFrames.get(i).isIntersectsWith(timeFrames.get(j))) {
                    String errorMessage = String.format("Timeframes %s and %s intersects",
                            timeFrames.get(i),
                            timeFrames.get(j));
                    log.error(errorMessage);
                    throw new ValidationException(errorMessage);
                }
            }
        }
    }

    private void checkTimeframesOrder(List<TimeFrame> timeFrames) throws ValidationException {
        for (int i = 0; i < timeFrames.size() - 1; ++i) {
            LocalDateTime firstTimeframeStartTime = timeFrames.get(i).getStartTime();
            LocalDateTime secondTimeframeStartTime = timeFrames.get(i+1).getStartTime();
            if (secondTimeframeStartTime.isBefore(firstTimeframeStartTime)) {
                String errorMessage = String.format("Timeframes must be ordered. " +
                        "Timeframe %s must be after and %s", secondTimeframeStartTime, firstTimeframeStartTime);
                log.error(errorMessage);
                throw new ValidationException(errorMessage);
            }
        }
    }

}
