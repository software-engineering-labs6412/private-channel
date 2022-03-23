package org.ssau.privatechannel.model;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.Map;

import static org.ssau.privatechannel.model.ConfidentialInfo.Queries;
import static org.ssau.privatechannel.model.ConfidentialInfo.QueryNames;

@Entity
@Table(name = ConfidentialInfo.Tables.CONFIDENTIAL_INFORMATION)
@NamedQuery(
        name = QueryNames.GET_ALL_INFO,
        query = Queries.GET_ALL_INFO)
@NamedNativeQuery(
        name = QueryNames.GET_BATCH_INFO,
        query = Queries.GET_BATCH_INFO,
        resultClass = ConfidentialInfo.class)
@NamedQuery(
        name = QueryNames.DELETE_BATCH,
        query = Queries.DELETE_BATCH)
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class ConfidentialInfo {

    public static abstract class Queries {
        public static final String GET_ALL_INFO = "select info from ConfidentialInfo info";
        public static final String GET_BATCH_INFO = "select inf.record_id, inf.text_data, inf.receiver_ip, inf.sender_ip from conf_info inf limit 10";
        public static final String DELETE_BATCH = "delete from ConfidentialInfo info where info.id in (:ids)";
    }

    public static abstract class QueryNames {
        public static final String GET_ALL_INFO = "ConfidentialInfo.findAll";
        public static final String GET_BATCH_INFO = "ConfidentialInfo.getBatch";
        public static final String DELETE_BATCH = "ConfidentialInfo.deleteBatch";
    }

    public static abstract class Tables {
        public static final String CONFIDENTIAL_INFORMATION = "conf_info";
    }

    private static abstract class Columns {
        public static final String RECORD_ID = "record_id";
        public static final String SENDER_IP = "sender_ip";
        public static final String RECEIVER_IP = "receiver_ip";
        public static final String TEXT_DATA = "text_data";
    }

    public ConfidentialInfo() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = Columns.RECORD_ID, nullable = false)
    private Long id;

    @Type(type = "jsonb")
    @Column(name = Columns.TEXT_DATA, columnDefinition = "json")
    private Map<String, Object> data;

    @Column(name = Columns.SENDER_IP, nullable = false)
    private String senderIP;

    @Column(name = Columns.RECEIVER_IP, nullable = false)
    private String receiverIP;

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
}
