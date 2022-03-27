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
import java.util.List;

import static org.ssau.privatechannel.model.Schedule.Queries;
import static org.ssau.privatechannel.model.Schedule.QueryNames;

@Entity
@Table(name = Schedule.Tables.SCHEDULE)
@NamedQuery(name = QueryNames.FIND_ALL,
        query = Queries.FIND_ALL)
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {

    public static abstract class Queries {
        public static final String FIND_ALL =
                "select distinct s from Schedule s";
    }

    public static abstract class QueryNames {
        public static final String FIND_ALL = "Schedule.findAll";
    }

    public static abstract class Tables {
        public static final String SCHEDULE = "schedule";
    }

    public static abstract class Columns {
        public static final String SCHEDULE_ID = "schedule_id";
        public static final String TIME_END = "time_end";
    }

    private static final String DATE_PATTERN = "dd-MM-yyyy HH:mm:ss";
    private static final String TIMEZONE = "Europe/Samara";

    @Id
    @Column(name = Columns.SCHEDULE_ID, nullable = false)
    private Long id;

    @Column(name = Columns.TIME_END)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN, timezone = TIMEZONE)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime timeEnd;

    @OneToMany(mappedBy = Tables.SCHEDULE, orphanRemoval = true, cascade = CascadeType.MERGE)
    private List<TimeFrame> timeFrames;

    public LocalDateTime getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(LocalDateTime timeEnd) {
        this.timeEnd = timeEnd;
    }

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
}