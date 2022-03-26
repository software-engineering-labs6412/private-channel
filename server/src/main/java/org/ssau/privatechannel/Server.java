package org.ssau.privatechannel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestTemplate;
import org.ssau.privatechannel.constants.SystemProperties;
import org.ssau.privatechannel.ui.StartPage;
import org.ssau.privatechannel.utils.ApplicationInstaller;

import java.io.IOException;

import static org.ssau.privatechannel.utils.ApplicationInstaller.Mode;
import static org.ssau.privatechannel.utils.DbClusterInstaller.Instances;

@SpringBootApplication
public class Server {

    private static final String CURRENT_INSTANCE = "Server";

    public static void main(String[] args) {

        System.setProperty(SystemProperties.INSTANCE, CURRENT_INSTANCE);


        try {
            StartPage.show();
        } catch (IOException e) {
            throw new RuntimeException("Could not show start page", e);
        }

        try {
            ApplicationInstaller.run(Instances.SERVER);
        } catch (Exception e) {
            throw new RuntimeException("Something wrong during server start: ", e);
        }
        SpringApplication.run(Server.class);
    }
}
