package org.ssau.privatechannel.model;

import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

import static org.ssau.privatechannel.model.Schedule.Queries;
import static org.ssau.privatechannel.model.Schedule.QueryNames;

@Entity
@Table(name = Schedule.Tables.SCHEDULE)
@NamedQuery(name = QueryNames.FIND_ALL,
        query = Queries.FIND_ALL)
@NamedQuery(name = QueryNames.FIND_FIRST_BY_IP,
        query = Queries.FIND_FIRST_BY_IP)
@NoArgsConstructor
@ToString
public class Schedule implements Serializable {

    private static final String DATE_PATTERN = "dd-MM-yyyy HH:mm:ss";
    private static final String TIMEZONE = "Europe/Samara";

    @Id
    @Column(name = Columns.SCHEDULE_ID, nullable = false)
    private Long id;

    @OneToMany(mappedBy = "id", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TimeFrame> timeFrames;

    @Column(name = Columns.CLIENT_IP)
    private String clientIp;

    public List<TimeFrame> getTimeFrames() {
        return timeFrames;
    }

    public void setTimeFrames(List<TimeFrame> time_frames) {
        this.timeFrames = time_frames;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public static abstract class Queries {
        public static final String FIND_ALL =
                "select distinct s from Schedule s";
        public static final String FIND_FIRST_BY_IP =
                "select distinct s from Schedule s where s.clientIp = :ip";
    }

    public static abstract class QueryNames {
        public static final String FIND_ALL = "Schedule.findAll";
        public static final String FIND_FIRST_BY_IP = "Schedule.findFirstByIp";
    }

    public static abstract class Tables {
        public static final String SCHEDULE = "schedule";
    }

    public static abstract class Columns {
        public static final String SCHEDULE_ID = "schedule_id";
        public static final String TIME_END = "time_end";
        public static final String CLIENT_IP = "client_ip";
    }
}