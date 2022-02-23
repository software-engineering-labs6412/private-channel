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
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "schedule")
@NamedQuery(name = "Schedule.findAllWithTimeFrames",
        query = "select distinct s from Schedule s left join fetch s.timeFrames")
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id", nullable = false)
    private Long id;

    // time of the end of the schedule
    @Column(name = "time_end")
    private Date timeEnd;

    @OneToMany(mappedBy = "schedule", orphanRemoval = true)
    private Set<TimeFrame> timeFrames;

    public Date getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(Date timeEnd) {
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