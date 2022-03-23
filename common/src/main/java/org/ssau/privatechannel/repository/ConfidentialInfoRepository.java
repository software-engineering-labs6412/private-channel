package org.ssau.privatechannel.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.ssau.privatechannel.model.ConfidentialInfo;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ConfidentialInfoRepository extends AbstractRepository {

    private static class NamedQueries {
        public static final String FIND_ALL = "ConfidentialInfo.findAll";
        public static final String GET_BATCH = "ConfidentialInfo.getBatch";
        public static final String DELETE_BATCH = "ConfidentialInfo.deleteBatch";
    }

    public Collection<ConfidentialInfo> findAll() {
        return entityManager.createNamedQuery(NamedQueries.FIND_ALL,
                ConfidentialInfo.class).getResultList();
    }

    public Collection<ConfidentialInfo> nextBatch() {
        return entityManager.createNamedQuery(NamedQueries.GET_BATCH,
                ConfidentialInfo.class).getResultList();
    }

    @Transactional
    public void deleteBatch(Collection<ConfidentialInfo> batch) {
        List<String> ids = batch.stream().map(elem -> elem.getId().toString()).collect(Collectors.toList());
        String batchAsParameter = String.join(", ", ids);
        entityManager.createNamedQuery(NamedQueries.DELETE_BATCH,
                ConfidentialInfo.class).setParameter("ids", batchAsParameter).executeUpdate();
    }

    @Transactional
    public void add(ConfidentialInfo info) {
        entityManager.merge(info);
    }

    @Transactional
    public void delete(ConfidentialInfo info) {
        entityManager.remove(info);
    }
}
