package org.ssau.privatechannel.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ssau.privatechannel.constants.Parameters;
import org.ssau.privatechannel.model.Schedule;
import org.ssau.privatechannel.model.TimeFrame;
import org.ssau.privatechannel.repository.ScheduleRepository;
import org.ssau.privatechannel.repository.TimeFrameRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@Slf4j
@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final TimeFrameRepository timeFrameRepository;

    private static final Random RANDOMIZER = new Random();

    @Autowired
    public ScheduleService(ScheduleRepository scheduleRepository,
                           TimeFrameRepository timeFrameRepository) {
        this.scheduleRepository = scheduleRepository;
        this.timeFrameRepository = timeFrameRepository;
    }

    public Schedule findNextByIp(String ip) {
        Schedule schedule = scheduleRepository.findNextForIp(ip);

        if (Objects.isNull(schedule)) {
            log.info("New schedule for client with ip = {} not found", ip);
            return null;
        }

        List<TimeFrame> timeFrames = timeFrameRepository.findAllForSchedule(schedule.getId());
        schedule.setTimeFrames(timeFrames);
        log.info("New schedule for client with IP = {} is: {}", ip, schedule);
        return schedule;
    }

    public void add(Schedule schedule) {
        if (Objects.isNull(schedule.getId())) {
            schedule.setId(Math.abs(RANDOMIZER.nextLong()) % Parameters.MAX_ID);
        }

        for (TimeFrame timeFrame : schedule.getTimeFrames()) {
            timeFrame.setSchedule(schedule);
        }

        scheduleRepository.add(schedule);
    }

    public void addAll(List<Schedule> schedules) {
        for (Schedule schedule : schedules) {
            if (Objects.isNull(schedule.getId())) {
                schedule.setId(Math.abs(RANDOMIZER.nextLong()) % Parameters.MAX_ID);
            }
            for (TimeFrame timeFrame : schedule.getTimeFrames()) {
                timeFrame.setSchedule(schedule);
            }
        }

        scheduleRepository.addAll(schedules);
    }

    @Transactional
    public void delete(Schedule schedule) {
        List<TimeFrame> timeFrames = timeFrameRepository.findAllForSchedule(schedule.getId());
        timeFrameRepository.deleteAll(timeFrames);
        scheduleRepository.delete(schedule);
    }

    public boolean isActualSchedule(Schedule schedule) {
        for (TimeFrame timeFrame : schedule.getTimeFrames()) {
            if (timeFrame.getStartTime().isBefore(LocalDateTime.now()))
                return false;
        }
        return true;
    }
}
