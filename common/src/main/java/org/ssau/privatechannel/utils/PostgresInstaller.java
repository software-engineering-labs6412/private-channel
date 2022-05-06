package org.ssau.privatechannel.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class PostgresInstaller {

    private static final String DB_INSTALLATION_JAR_PATH = "installation/db_installation-1.0.jar";
    private static final String SETTINGS_FILE_PATH = "installation/default_settings.json";
    private static final String DEFAULT_USERNAME = "postgres";
    private static final String DEFAULT_PASSWORD = "postgres";
    private static final String DEFAULT_DB_NAME = "private_channel";
    private static final Integer MAX_WAIT_TIME_SECONDS = 600;
    private static final Integer DEFAULT_DELAY_IN_SECONDS = 10;

    public static void run(String containerName, String port) throws IOException, InterruptedException {

        log.info("Start container [name = {}] creation with timeout = {}", containerName, MAX_WAIT_TIME_SECONDS);
        Thread thread = new Thread() {
            @SneakyThrows
            @Override
            public void run() {
                createSettingsFile(containerName, port);
                String command = String.format(Commands.START_CONTAINER_INSTALLATION,
                        DB_INSTALLATION_JAR_PATH,
                        SETTINGS_FILE_PATH);
                CommandRunner.run(command);
            }
        };

        thread.start();

        int timeSpent = 0;
        while (true) {
            Thread.sleep(TimeUnit.SECONDS.toMillis(DEFAULT_DELAY_IN_SECONDS));
            timeSpent += DEFAULT_DELAY_IN_SECONDS;

            log.info("Wait for container {} started...", containerName);
            List<String> consoleOutput =
                    CommandRunner.runQuietWithReturn(String.format(Commands.GET_CONTAINER_INFO, containerName));

            if (consoleOutput.size() > 1) {
                log.info("Database container \"{}\" started", containerName);
                thread.interrupt();
                break;
            }

            if (timeSpent >= MAX_WAIT_TIME_SECONDS) {
                String errorMessage = "Timeout reached during container creation";
                log.error(errorMessage);
                throw new InterruptedException(errorMessage);
            }
        }
        log.info("Container \"{}\" created and started", containerName);
    }

    private static void createSettingsFile(String dbName, String port) throws IOException {
        ContainerSettings settings = getDefaultSettings(dbName, port);
        String settingsJson = new ObjectMapper().writeValueAsString(settings);
        FileWriter writer = new FileWriter(SETTINGS_FILE_PATH);
        writer.write(settingsJson);
        writer.close();
    }

    private static ContainerSettings getDefaultSettings(String instanceName, String port) {
        ContainerSettings settings = new ContainerSettings();
        settings.setInstanceName(instanceName);
        settings.setUser(DEFAULT_USERNAME);
        settings.setPassword(DEFAULT_PASSWORD);
        settings.setDb(DEFAULT_DB_NAME);
        settings.setPort(port);
        return settings;
    }

    private static abstract class Commands {
        public static final String START_CONTAINER_INSTALLATION
                = "java -jar %s -settingsFile=%s";
        public static final String GET_CONTAINER_INFO = "docker ps -f name=%s";
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    private static class ContainerSettings {
        private String instanceName;
        private String user;
        private String password;
        private String db;
        private String port;
    }
}
