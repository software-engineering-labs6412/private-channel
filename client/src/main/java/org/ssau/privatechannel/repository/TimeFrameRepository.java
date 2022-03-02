package org.ssau.privatechannel.repository;

import org.springframework.transaction.annotation.Transactional;
import org.ssau.privatechannel.model.TimeFrame;

import java.util.Collection;

public class TimeFrameRepository extends AbstractRepository {

    Collection<TimeFrame> findAll() {
        return entityManager.createNamedQuery("TimeFrame.findAllWithSchedule", TimeFrame.class).getResultList();
    }

    @Transactional
    void add(TimeFrame timeFrame) {
        entityManager.persist(timeFrame);
    }

    @Transactional
    void delete(TimeFrame timeFrame) {
        entityManager.remove(timeFrame);
    }

    @Transactional
    void edit(TimeFrame timeFrame) {
        entityManager.persist(timeFrame);
    }

}