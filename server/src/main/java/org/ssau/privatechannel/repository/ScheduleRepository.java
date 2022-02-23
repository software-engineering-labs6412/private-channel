package org.ssau.privatechannel.repository;

import org.springframework.transaction.annotation.Transactional;
import org.ssau.privatechannel.model.Schedule;

import java.util.Collection;

public class ScheduleRepository extends AbstractRepository {

    Collection<Schedule> findAll() {
        return entityManager.createNamedQuery("Schedule.findAllWithTimeFrames", Schedule.class).getResultList();
    }

    @Transactional
    void add(Schedule schedule) {
        entityManager.persist(schedule);
    }

    @Transactional
    void delete(Schedule schedule) {
        entityManager.remove(schedule);
    }

    @Transactional
    void edit(Schedule schedule) {
        entityManager.persist(schedule);
    }

}
