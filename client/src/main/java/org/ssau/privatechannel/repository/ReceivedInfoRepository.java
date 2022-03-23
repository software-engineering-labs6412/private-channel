package org.ssau.privatechannel.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.ssau.privatechannel.model.ReceivedInformation;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ReceivedInfoRepository extends AbstractRepository {

    private static abstract class QueryNames {
        public static final String FIND_ALL = "ReceivedInformation.findAll";
        public static final String NEXT_BATCH = "ReceivedInformation.getBatch";
        public static final String DELETE_BATCH = "ReceivedInformation.deleteBatch";
    }

    private static abstract class QueryParams {
        public static final String IDS = "ids";
    }

    public Collection<ReceivedInformation> findAll() {
        return entityManager.createNamedQuery(QueryNames.FIND_ALL,
                ReceivedInformation.class).getResultList();
    }

    public Collection<ReceivedInformation> nextBatch() {
        return entityManager.createNamedQuery(QueryNames.NEXT_BATCH,
                ReceivedInformation.class).getResultList();
    }

    @Transactional
    public void deleteBatch(Collection<ReceivedInformation> batch) {
        List<String> ids = batch.stream().map(elem -> elem.getId().toString()).collect(Collectors.toList());
        String batchAsParameter = String.join(", ", ids);
        entityManager.createNamedQuery(QueryNames.DELETE_BATCH,
                ReceivedInformation.class).setParameter(QueryParams.IDS, batchAsParameter).executeUpdate();
    }

    @Transactional
    public void add(ReceivedInformation info) {
        entityManager.merge(info);
    }

    @Transactional
    public void delete(ReceivedInformation info) {
        entityManager.remove(info);
    }
}
