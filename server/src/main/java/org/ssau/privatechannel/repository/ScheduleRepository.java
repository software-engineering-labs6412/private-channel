package org.ssau.privatechannel.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.ssau.privatechannel.model.Schedule;

import java.util.Collection;

@Repository
public class ScheduleRepository extends AbstractRepository {

    public Collection<Schedule> findAll() {
        return entityManager.createNamedQuery("Schedule.findAllWithTimeFrames", Schedule.class).getResultList();
    }

    @Transactional
    public void add(Schedule schedule) {
        entityManager.persist(schedule);
    }

    @Transactional
    public void delete(Schedule schedule) {
        entityManager.remove(schedule);
    }

    @Transactional
    public void edit(Schedule schedule) {
        entityManager.persist(schedule);
    }

}
