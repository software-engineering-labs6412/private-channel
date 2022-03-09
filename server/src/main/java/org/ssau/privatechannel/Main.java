package org.ssau.privatechannel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.ssau.privatechannel.constants.SystemProperties;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        System.setProperty(SystemProperties.CLIENT1_IP, args[1]);
        System.setProperty(SystemProperties.CLIENT2_IP, args[2]);
        System.setProperty(SystemProperties.SERVER_IP, args[3]);
        SpringApplication.run(Main.class);
    }
}
