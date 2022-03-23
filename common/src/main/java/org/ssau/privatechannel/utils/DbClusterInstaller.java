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

    private static final Integer DEFAULT_CLIENTS_COUNT = 2;

    public static void singleInstall(String instance) throws IOException, InvalidInstanceTypeException {

        if (!instance.equals(Instances.SERVER) && !instance.equals(Instances.CLIENT)) {
            log.error("Invalid instance type provided during database deployment: {}. Must be {} or {}",
                    instance, Instances.SERVER, Instances.CLIENT);
            throw new InvalidInstanceTypeException("DB Installation must be only for server or client instance");
        }

        String dbUrl = System.getProperty(SystemProperties.DB_URL);
        String dbPort = System.getProperty(SystemProperties.DB_PORT);
        System.setProperty(SystemProperties.DB_URL, String.format(dbUrl, dbPort));

        List<String> consoleOutput =
                CommandRunner.runWithReturn(String.format(Commands.GET_CONTAINER_INFO, instance));

        if (consoleOutput.size() > 1) {
            log.info("Database container \"{}\" already exist", instance);
            String containerInfo = consoleOutput.get(1);
            runContainerIfStopped(containerInfo);
        } else {
            log.info("Database container \"{}\" not exist. Starting database installation...", instance);

            String port = System.getProperty(SystemProperties.DB_PORT);
            PostgresInstaller.run(instance, port);
        }
    }

    public static void run() throws IOException, InvalidInstanceTypeException {

        singleInstall(Instances.SERVER);

        String dbUrl = System.getProperty(SystemProperties.DB_URL);
        String dbPort = System.getProperty(SystemProperties.DB_PORT);
        System.setProperty(SystemProperties.DB_URL, String.format(dbUrl, dbPort));

        String port = System.getProperty(SystemProperties.DB_PORT);
        String currentClientPort = String.valueOf(Integer.parseInt(port));
        for (int i = 0; i < DEFAULT_CLIENTS_COUNT; ++i) {
            currentClientPort = String.valueOf(Integer.parseInt(currentClientPort) + 1);

            String currentInstance = String.format("%s_%s", Instances.CLIENT, i);
            List<String> consoleOutput = CommandRunner.runWithReturn(String.format(Commands.GET_CONTAINER_INFO, currentInstance));

            // Current client db container exists
            if (consoleOutput.size() > 1) {
                log.info("Database container \"{}\" already exist", currentInstance);
                String containerInfo = consoleOutput.get(1);
                runContainerIfStopped(containerInfo);
            } else {
                log.info("Database container \"{}\" not exist. Starting database installation...", currentInstance);
                PostgresInstaller.run(currentInstance, currentClientPort);
            }
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
