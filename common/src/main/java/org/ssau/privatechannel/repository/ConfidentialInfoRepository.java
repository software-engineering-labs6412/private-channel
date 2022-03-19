package org.ssau.privatechannel.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.ssau.privatechannel.model.ConfidentialInfo;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ConfidentialInfoRepository extends AbstractRepository {

    public Collection<ConfidentialInfo> findAll() {
        return entityManager.createNamedQuery("ConfidentialInfo.findAll",
                ConfidentialInfo.class).getResultList();
    }

    public Collection<ConfidentialInfo> nextBatch() {
        return entityManager.createNamedQuery("ConfidentialInfo.getBatch",
                ConfidentialInfo.class).getResultList();
    }

    @Transactional
    public void deleteBatch(Collection<ConfidentialInfo> batch) {
        List<String> ids = batch.stream().map(elem -> elem.getId().toString()).collect(Collectors.toList());
        String batchAsParameter = String.join(", ", ids);
        entityManager.createNamedQuery("ConfidentialInfo.deleteBatch",
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
