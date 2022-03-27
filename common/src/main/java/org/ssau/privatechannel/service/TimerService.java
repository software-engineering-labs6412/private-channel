package org.ssau.privatechannel.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

@Slf4j
@Service
public class TimerService {

    public void createTask(TimerTask task, LocalDateTime time) {
        Date fireMoment = Date.from(time.atZone(ZoneId.systemDefault()).toInstant());
        new Timer().schedule(task, fireMoment);
    }
}
