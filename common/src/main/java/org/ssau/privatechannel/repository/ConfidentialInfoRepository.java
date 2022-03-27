package org.ssau.privatechannel.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.ssau.privatechannel.model.ConfidentialInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Repository
public class ConfidentialInfoRepository extends AbstractRepository {

    public Collection<ConfidentialInfo> findAll() {
        return entityManager.createNamedQuery(NamedQueries.FIND_ALL,
                ConfidentialInfo.class).getResultList();
    }

    public Collection<ConfidentialInfo> findAllByIds(List<Long> ids) {
        return entityManager.createNamedQuery(NamedQueries.FIND_ALL_BY_IDS,
                ConfidentialInfo.class).setParameter(QueryParams.IDS, ids).getResultList();
    }

    public Collection<ConfidentialInfo> nextBatch() {
        return entityManager.createNamedQuery(NamedQueries.GET_BATCH,
                ConfidentialInfo.class).getResultList();
    }

    @Transactional
    public void deleteBatch(Collection<ConfidentialInfo> batch) {
        List<Long> ids = new ArrayList<>();

        for (ConfidentialInfo record : batch) {
            ids.add(record.getId());
        }

        Collection<ConfidentialInfo> allByIds = findAllByIds(ids);

        for (ConfidentialInfo currentRecord : allByIds) {
            entityManager.remove(currentRecord);
        }
    }

    @Transactional
    public void add(ConfidentialInfo info) {
        entityManager.merge(info);
    }

    @Transactional
    public void addAll(List<ConfidentialInfo> info) {
        for (ConfidentialInfo currentRecord : info) {
            entityManager.merge(currentRecord);
        }
    }

    @Transactional
    public void delete(ConfidentialInfo info) {
        entityManager.remove(info);
    }

    private static class NamedQueries {
        public static final String FIND_ALL = "ConfidentialInfo.findAll";
        public static final String FIND_ALL_BY_IDS = "ConfidentialInfo.findAllByIds";
        public static final String GET_BATCH = "ConfidentialInfo.getBatch";
    }

    private static abstract class QueryParams {
        public static final String IDS = "ids";
    }
}
