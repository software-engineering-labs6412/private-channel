package org.ssau.privatechannel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.ssau.privatechannel.constants.SystemProperties;
import org.ssau.privatechannel.utils.ApplicationInstaller;

import java.io.IOException;

import static org.ssau.privatechannel.utils.ApplicationInstaller.Mode;
import static org.ssau.privatechannel.utils.DbClusterInstaller.Instances;

@SpringBootApplication
public class Client {
    public static void main(String[] args) {
/*        System.setProperty(SystemProperties.NEIGHBOUR_IP, args[1].split("=")[1]);
        System.setProperty(SystemProperties.SERVER_IP, args[2].split("=")[1]);*/
        try {
            String dbPort = "7430";
            System.setProperty(SystemProperties.DB_PORT, dbPort);
            ApplicationInstaller.run(Mode.SINGLE_DB, Instances.CLIENT);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        SpringApplication.run(Client.class, args);
    }
}