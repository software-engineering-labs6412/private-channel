package org.ssau.privatechannel.repository;

import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;

public class AbstractRepository {

    @Autowired
    protected EntityManager entityManager;
}
