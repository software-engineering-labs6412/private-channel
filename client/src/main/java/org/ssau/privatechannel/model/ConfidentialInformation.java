package org.ssau.privatechannel.model;

import javax.persistence.*;

@Entity
@Table(name = ConfidentialInformation.Tables.CONFIDENTIAL_INFORMATION)
@NamedQuery(
        name = "ConfidentialInformation.findAll",
        query = ConfidentialInformation.Queries.GET_ALL_INFO)
@NamedNativeQuery(
        name = "ConfidentialInformation.getBatch",
        query = ConfidentialInformation.Queries.GET_BATCH_INFO,
        resultClass = ConfidentialInformation.class)
@NamedQuery(
        name = "ConfidentialInformation.deleteBatch",
        query = ConfidentialInformation.Queries.DELETE_BATCH)
public class ConfidentialInformation {

    public static abstract class Queries {
        public static final String GET_ALL_INFO = "select info from ConfidentialInformation info";
        public static final String GET_BATCH_INFO = "select inf.record_id, inf.text_data from conf_info inf limit 10";
        public static final String DELETE_BATCH = "delete from ConfidentialInformation info where info.id in (:ids)";
    }

    public static abstract class Tables {
        public static final String CONFIDENTIAL_INFORMATION = "conf_info";
    }

    private static abstract class Columns {
        public static final String RECORD_ID = "record_id";
        public static final String TEXT_DATA = "text_data";
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = Columns.RECORD_ID, nullable = false)
    private Long id;

    @Column(name = Columns.TEXT_DATA)
    private String textData;

    public ConfidentialInformation() {
    }

    public ConfidentialInformation(Long id, String textData) {
        this.id = id;
        this.textData = textData;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTextData() {
        return textData;
    }

    public void setTextData(String textData) {
        this.textData = textData;
    }
}
