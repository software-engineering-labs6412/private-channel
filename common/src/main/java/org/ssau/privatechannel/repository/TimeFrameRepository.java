package org.ssau.privatechannel.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.ssau.privatechannel.model.TimeFrame;

import java.util.Collection;

@Repository
public class TimeFrameRepository extends AbstractRepository {

    private static class NamedQueries {
        public static final String FIND_ALL_WITH_SCHEDULE = "TimeFrame.findAllWithSchedule";
    }

    public Collection<TimeFrame> findAll() {
        return entityManager.createNamedQuery(NamedQueries.FIND_ALL_WITH_SCHEDULE, TimeFrame.class).getResultList();
    }

    @Transactional
    public void add(TimeFrame timeFrame) {
        entityManager.persist(timeFrame);
    }

    @Transactional
    public void delete(TimeFrame timeFrame) {
        entityManager.remove(timeFrame);
    }

    @Transactional
    public void edit(TimeFrame timeFrame) {
        entityManager.persist(timeFrame);
    }

}
