package org.ssau.privatechannel.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.ssau.privatechannel.model.Schedule;
import org.ssau.privatechannel.model.TimeFrame;

import java.util.List;

@Repository
public class TimeFrameRepository extends AbstractRepository {

    @SuppressWarnings("unchecked")
    public List<TimeFrame> findAllForSchedule(Long scheduleId) {
        return entityManager.createNativeQuery("select * from time_frame " +
                        "where schedule_id = :schedule_id", TimeFrame.class)
                .setParameter(QueryParams.SCHEDULE_ID, scheduleId).getResultList();
    }

    private static class QueryParams {
        public static final String SCHEDULE_ID = "schedule_id";
    }

    public void deleteAll(List<TimeFrame> timeFrames) {
        for (TimeFrame timeFrame : timeFrames)
            entityManager.remove(timeFrame);
    }

}
