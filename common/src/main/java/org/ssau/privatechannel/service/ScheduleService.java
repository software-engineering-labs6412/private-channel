package org.ssau.privatechannel.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ssau.privatechannel.model.Schedule;
import org.ssau.privatechannel.model.TimeFrame;
import org.ssau.privatechannel.repository.ScheduleRepository;
import org.ssau.privatechannel.repository.TimeFrameRepository;

import java.util.Collection;
import java.util.List;

@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final TimeFrameRepository timeFrameRepository;

    @Autowired
    public ScheduleService(ScheduleRepository scheduleRepository,
                           TimeFrameRepository timeFrameRepository) {
        this.scheduleRepository = scheduleRepository;
        this.timeFrameRepository = timeFrameRepository;
    }

    public Schedule findNextByIp(String ip) {
        Schedule schedule = scheduleRepository.findNextForIp(ip);
        List<TimeFrame> timeFrames = timeFrameRepository.findAllForSchedule(schedule.getId());
        schedule.setTimeFrames(timeFrames);
        return schedule;
    }

    public void add(Schedule schedule) {
        scheduleRepository.add(schedule);
    }

    public void delete(Schedule schedule) {
        scheduleRepository.delete(schedule);
    }

    public void edit(Schedule schedule) {
        scheduleRepository.edit(schedule);
    }

}
