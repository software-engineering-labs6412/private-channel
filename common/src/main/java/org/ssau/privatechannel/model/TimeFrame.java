package org.ssau.privatechannel.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

import static org.ssau.privatechannel.model.TimeFrame.Queries;
import static org.ssau.privatechannel.model.TimeFrame.QueryNames;

@Entity
@Table(name = TimeFrame.Tables.TIME_FRAME)
@NamedNativeQuery(name = QueryNames.SELECT_TIMEFRAMES_FOR_SCHEDULE, query = Queries.SELECT_TIMEFRAMES_FOR_SCHEDULE)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Getter
@Setter
@RequiredArgsConstructor
public class TimeFrame implements Serializable {
    private static final String DATE_PATTERN = "dd-MM-yyyy HH:mm:ss";
    private static final String TIMEZONE = "Europe/Samara";

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

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    public boolean isIntersectsWith(TimeFrame timeFrame) {
        return isMomentInTimeFrame(timeFrame.startTime) || isMomentInTimeFrame(timeFrame.endTime) ||
                timeFrame.isMomentInTimeFrame(startTime) || timeFrame.isMomentInTimeFrame(endTime);
    }

    public boolean isMomentInTimeFrame(LocalDateTime moment) {
        return moment.isAfter(startTime) && moment.isBefore(endTime);
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

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public static abstract class Queries {
        public static final String SELECT_TIMEFRAMES_FOR_SCHEDULE =
                "select * from time_frame where schedule_id = :schedule_id";
    }

    public static abstract class QueryNames {
        public static final String SELECT_TIMEFRAMES_FOR_SCHEDULE = "TimeFrame.findAllWithSchedule";
    }

    public static abstract class Tables {
        public static final String TIME_FRAME = "time_frame";
    }

    private static abstract class Columns {
        public static final String TIME_FRAME_ID = "time_frame_id";
        public static final String START_TIME = "start_time";
        public static final String END_TIME = "end_time";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        TimeFrame timeFrame = (TimeFrame) o;
        return id != null && Objects.equals(id, timeFrame.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}