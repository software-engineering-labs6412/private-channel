package org.ssau.privatechannel.utils;

import lombok.extern.slf4j.Slf4j;
import org.ssau.privatechannel.constants.SystemProperties;
import org.ssau.privatechannel.exception.InvalidInstanceTypeException;

import javax.swing.*;
import java.io.IOException;
import java.util.List;

@Slf4j
public class DbClusterInstaller {

    private static class Commands {
        public static final String GET_CONTAINER_INFO = "docker ps -f name=%s -a";
        public static final String START_CONTAINER_BY_ID = "docker start %s";
    }

    public static class Instances {
        public static final String SERVER = "server_db";
        public static final String CLIENT = "client_db";
    }

    private static class ContainerStatuses {
        public static final String EXITED = "Exited";
    }

    private static final String DEFAULT_CONTAINER_PREFIX = "pc";

    public static void singleInstall(String instance) throws IOException, InvalidInstanceTypeException, InterruptedException {

        if (!instance.equals(Instances.SERVER) && !instance.equals(Instances.CLIENT)) {
            log.error("Invalid instance type provided during database deployment: {}. Must be {} or {}",
                    instance, Instances.SERVER, Instances.CLIENT);
            throw new InvalidInstanceTypeException("DB Installation must be only for server or client instance");
        }

        String dbUrl = System.getProperty(SystemProperties.DB_URL);
        String dbPort = System.getProperty(SystemProperties.DB_PORT);
        String containerName = String.format("%s_%s", DEFAULT_CONTAINER_PREFIX, dbPort);

        System.setProperty(SystemProperties.DB_URL, String.format(dbUrl, dbPort));

        List<String> consoleOutput =
                CommandRunner.runWithReturn(String.format(Commands.GET_CONTAINER_INFO, containerName));

        if (consoleOutput.size() > 1) {
            log.info("Database container \"{}\" already exist", containerName);
            String containerInfo = consoleOutput.get(1);
            runContainerIfStopped(containerInfo);
        } else {
            log.info("Database container \"{}\" not exist. Starting database installation...", containerName);

            String port = System.getProperty(SystemProperties.DB_PORT);
            PostgresInstaller.run(containerName, port);
        }
    }

    private static void runContainerIfStopped(String containerInfo) throws IOException {
        if (!containerInfo.contains(ContainerStatuses.EXITED)) {
            log.info("Container already running");
            return;
        }

        log.info("Container exist but not running. Starting container...");
        String containerId = containerInfo.split(" ")[0];
        CommandRunner.run(String.format(Commands.START_CONTAINER_BY_ID, containerId));
    }
}
