package org.ssau.privatechannel.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.ssau.privatechannel.model.ReceivedInformation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Repository
public class ReceivedInfoRepository extends AbstractRepository {

    @Transactional
    public void addAll(List<ReceivedInformation> info) {
        for (ReceivedInformation currentRecord : info) {
            entityManager.merge(currentRecord);
        }
    }
}
