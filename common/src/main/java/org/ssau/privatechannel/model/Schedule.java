package org.ssau.privatechannel.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

@Entity
@Table(name = Schedule.Tables.SCHEDULE)
@NamedQuery(name = "Schedule.findAllWithTimeFrames",
        query = "select distinct s from Schedule s left join fetch s.timeFrames")
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {
    private static final String DATE_PATTERN = "dd-MM-yyyy HH:mm:ss";
    private static final String TIMEZONE = "Europe/Samara";

    public static abstract class Tables{
        public static final String SCHEDULE = "schedule";
    }


    private static abstract class Columns{
        public static final String SCHEDULE_ID = "schedule_id";
        public static final String TIME_END = "time_end";
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = Columns.SCHEDULE_ID, nullable = false)
    private Long id;

    @Column(name = Columns.TIME_END)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN, timezone = TIMEZONE)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime timeEnd;

    @OneToMany(mappedBy = Tables.SCHEDULE, orphanRemoval = true)
    private Set<TimeFrame> timeFrames;

    public LocalDateTime getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(LocalDateTime timeEnd) {
        this.timeEnd = timeEnd;
    }

    public Set<TimeFrame> getTimeFrames() {
        return timeFrames;
    }

    public void setTimeFrames(Set<TimeFrame> time_frames) {
        this.timeFrames = time_frames;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}