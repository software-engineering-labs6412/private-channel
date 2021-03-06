package org.ssau.privatechannel.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.ssau.privatechannel.model.Schedule;

import javax.persistence.NoResultException;
import java.util.List;
import java.util.Objects;

@Repository
public class ScheduleRepository extends AbstractRepository {

    public Schedule findById(Long id) {
        return entityManager.find(Schedule.class, id);
    }

    public Schedule findNextForIp(String ip) {
        try {
            List<Schedule> resultList = entityManager.createNamedQuery(NamedQueries.FIND_FIRST_BY_IP, Schedule.class)
                    .setParameter(QueryParams.IP, ip).getResultList();
            if (Objects.isNull(resultList) || resultList.isEmpty()) {
                return null;
            }
            return resultList.get(0);
        } catch (NoResultException e) {
            return null;
        }
    }

    @Transactional
    public void add(Schedule schedule) {
        entityManager.persist(schedule);
    }

    @Transactional
    public void addAll(List<Schedule> schedules) {
        for (Schedule currentRecord : schedules) {
            entityManager.persist(currentRecord);
        }
    }

    @Transactional
    public void delete(Schedule schedule) {
        Schedule scheduleForDelete = findById(schedule.getId());
        entityManager.remove(scheduleForDelete);
    }

    private static class NamedQueries {
        public static final String FIND_FIRST_BY_IP = "Schedule.findFirstByIp";
    }

    private static class QueryParams {
        public static final String IP = "ip";
    }

}
