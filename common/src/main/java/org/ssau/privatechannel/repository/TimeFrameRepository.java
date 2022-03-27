package org.ssau.privatechannel.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.ssau.privatechannel.model.TimeFrame;

import java.util.List;

@Repository
public class TimeFrameRepository extends AbstractRepository {

    public List<TimeFrame> findAllForSchedule(Long scheduleId) {
        return entityManager.createNamedQuery(NamedQueries.FIND_ALL_FOR_SCHEDULE, TimeFrame.class)
                .setParameter(QueryParams.SCHEDULE_ID, scheduleId).getResultList();
    }

    @Transactional
    public void add(TimeFrame timeFrame) {
        entityManager.merge(timeFrame);
    }

    @Transactional
    public void delete(TimeFrame timeFrame) {
        entityManager.remove(timeFrame);
    }

    @Transactional
    public void edit(TimeFrame timeFrame) {
        entityManager.merge(timeFrame);
    }

    private static class NamedQueries {
        public static final String FIND_ALL_FOR_SCHEDULE = "TimeFrame.findAllWithSchedule";
    }

    private static class QueryParams {
        public static final String SCHEDULE_ID = "schedule_id";
    }

}
