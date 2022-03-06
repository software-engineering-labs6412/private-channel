package org.ssau.privatechannel.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = Schedule.Tables.SCHEDULE)
@NamedQuery(name = "Schedule.findAllWithTimeFrames",
        query = "select distinct s from Schedule s left join fetch s.timeFrames")
public class Schedule {

    public static class Tables{
        public static final String SCHEDULE = "schedule";
    }


    private static class Columns{
        public static final String SCHEDULE_ID = "schedule_id";
        public static final String TIME_END = "time_end";
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = Columns.SCHEDULE_ID, nullable = false)
    private Long id;

    @Column(name = Columns.TIME_END)
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