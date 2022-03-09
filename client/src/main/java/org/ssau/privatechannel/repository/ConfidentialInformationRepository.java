package org.ssau.privatechannel.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.ssau.privatechannel.model.ConfidentialInformation;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ConfidentialInformationRepository extends AbstractRepository {

    public Collection<ConfidentialInformation> findAll() {
        return entityManager.createNamedQuery("ConfidentialInformation.findAll",
                ConfidentialInformation.class).getResultList();
    }

    public Collection<ConfidentialInformation> nextBatch() {
        return entityManager.createNamedQuery("ConfidentialInformation.getBatch",
                ConfidentialInformation.class).getResultList();
    }

    @Transactional
    public void deleteBatch(Collection<ConfidentialInformation> batch) {
        List<String> ids = batch.stream().map(elem -> elem.getId().toString()).collect(Collectors.toList());
        String batchAsParameter = String.join(", ", ids);
        entityManager.createNamedQuery("ConfidentialInformation.deleteBatch",
                ConfidentialInformation.class).setParameter("ids", batchAsParameter).executeUpdate();
    }

    @Transactional
    public void add(ConfidentialInformation info) {
        entityManager.persist(info);
    }

    @Transactional
    public void delete(ConfidentialInformation info) {
        entityManager.remove(info);
    }
}
