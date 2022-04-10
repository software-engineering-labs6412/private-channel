package org.ssau.privatechannel.model;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.Map;
import java.util.Objects;

import static org.ssau.privatechannel.model.ReceivedInformation.Queries;
import static org.ssau.privatechannel.model.ReceivedInformation.QueryNames;

@Entity
@Table(name = ReceivedInformation.Tables.CONFIDENTIAL_INFORMATION)
@NamedQuery(
        name = QueryNames.GET_ALL_INFO,
        query = Queries.GET_ALL_INFO)
@NamedNativeQuery(
        name = QueryNames.GET_BATCH_INFO,
        query = Queries.GET_BATCH_INFO,
        resultClass = ReceivedInformation.class)
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@ToString
@AllArgsConstructor
@Builder
@Getter
@Setter
public class ReceivedInformation {

    @Id
    @Column(name = Columns.RECORD_ID, nullable = false)
    private Long id;
    @Type(type = "jsonb")
    @Column(name = Columns.TEXT_DATA, columnDefinition = "json")
    private Map<String, Object> data;
    @Column(name = Columns.SENDER_IP, nullable = false)
    private String senderIP;
    @Column(name = Columns.RECEIVER_IP, nullable = false)
    private String receiverIP;

    public ReceivedInformation() {
    }

    public String getSenderIP() {
        return senderIP;
    }

    public void setSenderIP(String senderIP) {
        this.senderIP = senderIP;
    }

    public String getReceiverIP() {
        return receiverIP;
    }

    public void setReceiverIP(String receiverIP) {
        this.receiverIP = receiverIP;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public static abstract class Queries {
        public static final String GET_ALL_INFO = "select info from ReceivedInformation info";
        public static final String GET_BATCH_INFO = "select inf.record_id, inf.text_data from conf_info inf limit 10";
    }

    public static abstract class QueryNames {
        public static final String GET_ALL_INFO = "ReceivedInformation.findAll";
        public static final String GET_BATCH_INFO = "ReceivedInformation.getBatch";
    }

    public static abstract class Tables {
        public static final String CONFIDENTIAL_INFORMATION = "received_info";
    }

    private static abstract class Columns {
        public static final String RECORD_ID = "record_id";
        public static final String SENDER_IP = "sender_ip";
        public static final String RECEIVER_IP = "receiver_ip";
        public static final String TEXT_DATA = "text_data";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ReceivedInformation that = (ReceivedInformation) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
