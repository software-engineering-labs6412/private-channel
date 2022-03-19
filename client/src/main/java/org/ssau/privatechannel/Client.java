package org.ssau.privatechannel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class Client {
    public static void main(String[] args) {
/*        System.setProperty(SystemProperties.NEIGHBOUR_IP, args[1].split("=")[1]);
        System.setProperty(SystemProperties.SERVER_IP, args[2].split("=")[1]);*/
        SpringApplication.run(Client.class, args);
    }
}

// TODO подумать+реализовать сохранение данных в другую таблицу