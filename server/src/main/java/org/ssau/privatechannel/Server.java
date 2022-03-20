package org.ssau.privatechannel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.ssau.privatechannel.constants.SystemProperties;
import org.ssau.privatechannel.exception.DockerMissingException;
import org.ssau.privatechannel.exception.InvalidAppInstallationModeException;
import org.ssau.privatechannel.exception.InvalidInstanceTypeException;
import org.ssau.privatechannel.utils.ApplicationInstaller;

import java.io.IOException;

import static org.ssau.privatechannel.utils.ApplicationInstaller.Mode;
import static org.ssau.privatechannel.utils.DbClusterInstaller.Instances;

@SpringBootApplication
public class Server {
    public static void main(String[] args) {
/*        System.setProperty(SystemProperties.CLIENT1_IP, args[1]);
        System.setProperty(SystemProperties.CLIENT2_IP, args[2]);
        System.setProperty(SystemProperties.SERVER_IP, args[3]);*/
        try {
            String dbPort = "7430";
            System.setProperty(SystemProperties.DB_PORT, dbPort);
            ApplicationInstaller.run(Mode.SINGLE_DB, Instances.SERVER);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (DockerMissingException e) {
            e.printStackTrace();
        } catch (InvalidAppInstallationModeException e) {
            e.printStackTrace();
        } catch (InvalidInstanceTypeException e) {
            e.printStackTrace();
        }
        SpringApplication.run(Server.class);
    }
}
