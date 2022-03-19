package org.ssau.privatechannel.model;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.Map;

@Entity
@Table(name = ReceivedInformation.Tables.CONFIDENTIAL_INFORMATION)
@NamedQuery(
        name = "ReceivedInformation.findAll",
        query = ReceivedInformation.Queries.GET_ALL_INFO)
@NamedNativeQuery(
        name = "ReceivedInformation.getBatch",
        query = ReceivedInformation.Queries.GET_BATCH_INFO,
        resultClass = ReceivedInformation.class)
@NamedQuery(
        name = "ReceivedInformation.deleteBatch",
        query = ReceivedInformation.Queries.DELETE_BATCH)
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class ReceivedInformation {

    public static abstract class Queries {
        public static final String GET_ALL_INFO = "select info from ReceivedInformation info";
        public static final String GET_BATCH_INFO = "select inf.record_id, inf.text_data from conf_info inf limit 10";
        public static final String DELETE_BATCH = "delete from ReceivedInformation info where info.id in (:ids)";
    }

    public static abstract class Tables {
        public static final String CONFIDENTIAL_INFORMATION = "received_info";
    }

    private static abstract class Columns {
        public static final String RECORD_ID = "record_id";
        public static final String SENDER_IP = "sender_ip";
        public static final String RECEIVER_IP = "reveiver_ip";
        public static final String TEXT_DATA = "text_data";
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

    public ReceivedInformation() {
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