package org.ssau.privatechannel.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

@Service
public class TimerService {

    public void createTask (TimerTask task, LocalDateTime time) {

        Date fireMoment = Date.from(time.atZone(ZoneId.systemDefault()).toInstant());

        new Timer().schedule(task, fireMoment);

    }


}
