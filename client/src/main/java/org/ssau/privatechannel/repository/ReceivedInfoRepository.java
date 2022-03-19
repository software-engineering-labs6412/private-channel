package org.ssau.privatechannel.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.ssau.privatechannel.model.ReceivedInformation;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ReceivedInfoRepository extends AbstractRepository {


    // TODO findAll()
    public Collection<ReceivedInformation> findAll() {
        return entityManager.createNamedQuery("ReceivedInformation.findAll",
                ReceivedInformation.class).getResultList();
    }

    // TODO nextBatch()
    public Collection<ReceivedInformation> nextBatch() {
        return entityManager.createNamedQuery("ReceivedInformation.getBatch",
                ReceivedInformation.class).getResultList();
    }

    // TODO deleteBatch
    @Transactional
    public void deleteBatch(Collection<ReceivedInformation> batch) {
        List<String> ids = batch.stream().map(elem -> elem.getId().toString()).collect(Collectors.toList());
        String batchAsParameter = String.join(", ", ids);
        entityManager.createNamedQuery("ReceivedInformation.deleteBatch",
                ReceivedInformation.class).setParameter("ids", batchAsParameter).executeUpdate();
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
