package org.ssau.privatechannel.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = TimeFrame.Tables.TIME_FRAME)
@NamedQuery(name = "TimeFrame.findAllWithSchedule",
        query = TimeFrame.Queries.SELECT_ALL_TIMEFRAMES)
public class TimeFrame {

    public static abstract class Queries {
        public static final String SELECT_ALL_TIMEFRAMES = "select distinct t from TimeFrame t left join fetch t.schedule";
    }

    public static abstract class Tables{
        public static final String TIME_FRAME = "time_frame";
    }

    private static abstract class Columns {
        public static final String TIME_FRAME_ID = "time_frame_id";
        public static final String START_TIME = "start_time";
        public static final String END_TIME = "end_time";
        public static final String SCHEDULE_ID = "schedule_id";
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = Columns.TIME_FRAME_ID, nullable = false)
    private Long id;

    @Column(name = Columns.START_TIME)
    private LocalDateTime startTime;

    @Column(name = Columns.END_TIME)
    private LocalDateTime endTime;

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    @ManyToOne
    @JoinColumn(name = Columns.SCHEDULE_ID)
    private Schedule schedule;

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}