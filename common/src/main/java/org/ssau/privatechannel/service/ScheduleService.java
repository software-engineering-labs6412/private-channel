package org.ssau.privatechannel.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ssau.privatechannel.model.Schedule;
import org.ssau.privatechannel.repository.ScheduleRepository;

import java.util.Collection;

@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    @Autowired
    public ScheduleService(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    public Collection<Schedule> findAll() {
        return scheduleRepository.findAll();
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
