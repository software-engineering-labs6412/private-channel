package org.ssau.privatechannel.model;

import javax.persistence.*;

@Entity
@Table(name = AuthorizationKey.Tables.AUTHORIZATION_KEY)
@NamedQuery(
        name = "AuthorizationKey.get",
        query = AuthorizationKey.Queries.GET_KEY)
public class AuthorizationKey {

    @Id
    @Column(name = AuthorizationKey.Columns.ID, nullable = false)
    private Long id;
    @Column(name = AuthorizationKey.Columns.HASH, nullable = false)
    private String hash;

    public AuthorizationKey(Long id, String hash) {
        this.id = id;
        this.hash = hash;
    }

    public AuthorizationKey() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public static abstract class Queries {
        public static final String GET_KEY = "select key from AuthorizationKey key";
    }

    public static abstract class Tables {
        public static final String AUTHORIZATION_KEY = "authorization_key";
    }

    private static abstract class Columns {
        public static final String ID = "id";
        public static final String HASH = "hash";
    }
}
