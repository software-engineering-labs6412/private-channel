package org.ssau.privatechannel.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.ssau.privatechannel.model.ReceivedInformation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Repository
public class ReceivedInfoRepository extends AbstractRepository {

    public Collection<ReceivedInformation> findAll() {
        return entityManager.createNamedQuery(NamedQueries.FIND_ALL,
                ReceivedInformation.class).getResultList();
    }

    public Collection<ReceivedInformation> findAllByIds(List<Long> ids) {
        return entityManager.createNamedQuery(NamedQueries.FIND_ALL_BY_IDS,
                ReceivedInformation.class).setParameter(QueryParams.IDS, ids).getResultList();
    }

    public Collection<ReceivedInformation> nextBatch() {
        return entityManager.createNamedQuery(NamedQueries.NEXT_BATCH,
                ReceivedInformation.class).getResultList();
    }

    @Transactional
    public void deleteBatch(Collection<ReceivedInformation> batch) {
        List<Long> ids = new ArrayList<>();

        for (ReceivedInformation record : batch) {
            ids.add(record.getId());
        }

        Collection<ReceivedInformation> allByIds = findAllByIds(ids);

        for (ReceivedInformation currentRecord : allByIds) {
            entityManager.remove(currentRecord);
        }
    }

    @Transactional
    public void add(ReceivedInformation info) {
        entityManager.merge(info);
    }

    @Transactional
    public void addAll(List<ReceivedInformation> info) {
        for (ReceivedInformation currentRecord : info) {
            entityManager.merge(currentRecord);
        }
    }

    @Transactional
    public void delete(ReceivedInformation info) {
        entityManager.remove(info);
    }

    private static abstract class NamedQueries {
        public static final String FIND_ALL = "ReceivedInformation.findAll";
        public static final String NEXT_BATCH = "ReceivedInformation.getBatch";
        public static final String FIND_ALL_BY_IDS = "ReceivedInformation.findAllByIds";
    }

    private static abstract class QueryParams {
        public static final String IDS = "ids";
    }
}
