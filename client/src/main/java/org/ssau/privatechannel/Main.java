package org.ssau.privatechannel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.ssau.privatechannel.constants.SystemProperties;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        System.setProperty(SystemProperties.NEIGHBOUR_IP, args[1].split("=")[1]);
        System.setProperty(SystemProperties.SERVER_IP, args[2].split("=")[1]);
        SpringApplication.run(Main.class, args);
    }
}
