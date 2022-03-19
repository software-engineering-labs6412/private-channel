package org.ssau.privatechannel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Server {
    public static void main(String[] args) {
/*        System.setProperty(SystemProperties.CLIENT1_IP, args[1]);
        System.setProperty(SystemProperties.CLIENT2_IP, args[2]);
        System.setProperty(SystemProperties.SERVER_IP, args[3]);*/
        SpringApplication.run(Server.class);
    }
}
