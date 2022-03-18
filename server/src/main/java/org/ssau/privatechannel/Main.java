package org.ssau.privatechannel;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.ssau.privatechannel.constants.SystemProperties;
import org.ssau.privatechannel.model.Schedule;
import org.ssau.privatechannel.model.TimeFrame;
import org.ssau.privatechannel.utils.PostgresInstaller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
public class Main {
    public static void main(String[] args) throws IOException {
//        System.setProperty(SystemProperties.CLIENT1_IP, args[1]);
//        System.setProperty(SystemProperties.CLIENT2_IP, args[2]);
//        System.setProperty(SystemProperties.SERVER_IP, args[3]);
//        PostgresInstaller.run();
        SpringApplication.run(Main.class);
//        Schedule schedule = new Schedule();
//        TimeFrame timeFrame = new TimeFrame((long) 1, LocalDateTime.now(), LocalDateTime.now(), null);
//        Set<TimeFrame> timeFrameSet = new HashSet<>();
//        timeFrameSet.add(timeFrame);
//        schedule.setTimeFrames(timeFrameSet);
//        System.out.println(new ObjectMapper().writeValueAsString(schedule));

    }
}
