package org.ssau.privatechannel.model;

import org.hibernate.Hibernate;
import org.ssau.privatechannel.utils.Sha512EncoderService;

import javax.persistence.*;

import java.util.Objects;

import static org.ssau.privatechannel.model.AuthorizationKey.*;

@Entity
@Table(name = Tables.AUTHORIZATION_KEY)
@NamedQuery(
        name = QueryNames.GET_KEY,
        query = Queries.GET_KEY)
public class AuthorizationKey {

    @Id
    @Column(name = Columns.ID, nullable = false)
    private Long id;

    @Column(name = Columns.HASH, nullable = false)
    private String hash;

    public AuthorizationKey(Long id, String key) {
        this.id = id;
        this.hash = Sha512EncoderService.getHash(key);
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

    public static abstract class QueryNames {
        public static final String GET_KEY = "AuthorizationKey.get";
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        AuthorizationKey that = (AuthorizationKey) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
