package org.ssau.privatechannel.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

import static org.ssau.privatechannel.model.TimeFrame.Queries;
import static org.ssau.privatechannel.model.TimeFrame.QueryNames;

@Entity
@Table(name = TimeFrame.Tables.TIME_FRAME)
@NamedQuery(name = QueryNames.SELECT_ALL_TIMEFRAMES, query = Queries.SELECT_ALL_TIMEFRAMES)
@NoArgsConstructor
@AllArgsConstructor
public class TimeFrame {
    private static final String DATE_PATTERN = "dd-MM-yyyy HH:mm:ss";
    private static final String TIMEZONE = "Europe/Samara";

    public static abstract class Queries {
        public static final String SELECT_ALL_TIMEFRAMES = "select distinct t from TimeFrame t left join fetch t.schedule";
    }

    public static abstract class QueryNames {
        public static final String SELECT_ALL_TIMEFRAMES = "TimeFrame.findAllWithSchedule";
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
    @Column(name = Columns.TIME_FRAME_ID, nullable = false)
    private Long id;

    @Column(name = Columns.START_TIME)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN, timezone = TIMEZONE)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime startTime;

    @Column(name = Columns.END_TIME)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN, timezone = TIMEZONE)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime endTime;

    @ManyToOne
    @JoinColumn(name = Columns.SCHEDULE_ID, referencedColumnName = Schedule.Columns.SCHEDULE_ID)
    private Schedule schedule;

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

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