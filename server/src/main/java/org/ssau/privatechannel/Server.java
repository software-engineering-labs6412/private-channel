package org.ssau.privatechannel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.ssau.privatechannel.utils.ApplicationInstaller;
import org.ssau.privatechannel.utils.DockerInstaller;

import java.io.IOException;

@SpringBootApplication
public class Server {
    public static void main(String[] args) throws IOException, InterruptedException {
/*        System.setProperty(SystemProperties.CLIENT1_IP, args[1]);
        System.setProperty(SystemProperties.CLIENT2_IP, args[2]);
        System.setProperty(SystemProperties.SERVER_IP, args[3]);*/
        ApplicationInstaller.run();
        SpringApplication.run(Server.class);
    }
}
