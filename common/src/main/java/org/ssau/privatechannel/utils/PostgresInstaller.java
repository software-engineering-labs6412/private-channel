package org.ssau.privatechannel.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.FileWriter;
import java.io.IOException;

public class PostgresInstaller {

    private static abstract class Commands {
        public static final String START_CONTAINER_INSTALLATION
                = "java -jar %s -settingsFile=%s";
    }

    @Data
    @NoArgsConstructor
    private static class ContainerSettings {
        private String instanceName;
        private String user;
        private String password;
        private String db;
        private String port;
    }

    private static final String DB_INSTALLATION_JAR_PATH = "installation/db_installation-1.0.jar";
    private static final String SETTINGS_FILE_PATH = "installation/default_settings.json";

    private static final String DEFAULT_INSTANCE_NAME = "private_channel";
    private static final String DEFAULT_USERNAME = "postgres";
    private static final String DEFAULT_PASSWORD = "postgres";
    private static final String DEFAULT_DB_NAME = "private_channel";
    private static final String DEFAULT_PORT = "7433";

    public static void run() throws IOException {
        createSettingsFile();
        String command = String.format(Commands.START_CONTAINER_INSTALLATION,
                DB_INSTALLATION_JAR_PATH,
                SETTINGS_FILE_PATH);
        CommandRunner.runCommand(command);
    }

    private static void createSettingsFile() throws IOException {
        ContainerSettings settings = getDefaultSettings(DEFAULT_INSTANCE_NAME, DEFAULT_PORT);
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
}
