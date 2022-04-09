package org.ssau.privatechannel.utils;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.ssau.privatechannel.exception.DockerMissingException;

import javax.swing.*;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class DockerInstaller {

    private static final String DOCKER_DESKTOP_DOWNLOAD_LINK =
            "https://desktop.docker.com/win/main/amd64/Docker%20Desktop%20Installer.exe";
    private static final String INSTALLER_PATH = "installation/docker/";
    private static final String INSTALLER_FILE_NAME = "Docker Desktop Installer.exe";
    private static final String DOCKER_TASK_NAME = "Docker Desktop.exe";
    private static final Integer MAX_WAIT_TIME_SECONDS = 600;
    private static final Integer DEFAULT_DELAY_IN_SECONDS = 15;

    public static void run() throws IOException, InterruptedException, DockerMissingException {

        log.info("Docker starting with timeout {} ...", MAX_WAIT_TIME_SECONDS);
        boolean dockerStarted = startDocker();

        // Docker not installed
        if (!dockerStarted) {
            log.info("Docker Desktop not installed on current PC. Installation of docker desktop will be offer");
            boolean isDockerCanBeInstalled = showOfferDockerInstallationPopUp();

            if (isDockerCanBeInstalled) {
                log.info("Starting docker desktop installation...");
                CommandRunner.runQuiet(Commands.DOWNLOAD_DOCKER_DESKTOP); // Work
                CommandRunner.run(Commands.DOCKER_QUIET_INSTALL); // Work
                CommandRunner.run(Commands.DELETE_DOCKER_INSTALLER); // Work
                startDocker();
            } else {
                log.error("Docker will not be installed on current PC: user cancelled installation. Exiting...");
                throw new DockerMissingException("Docker-Desktop is required for private-channel application");
            }
        }
        log.info("Docker successfully started");
    }

    private static boolean startDocker() throws InterruptedException, IOException {

        final boolean[] isDockerInstalled = {true};

        Thread thread = new Thread() {
            @SneakyThrows
            @Override
            public void run() {
                String result = CommandRunner.runWithReturn(Commands.RUN_DOCKER).get(0);
                if (!result.equals("Successful"))
                    isDockerInstalled[0] = false;
            }
        };

        thread.start();

        int timeSpent = 0;
        while (true) {
            Thread.sleep(TimeUnit.SECONDS.toMillis(DEFAULT_DELAY_IN_SECONDS));

            timeSpent += DEFAULT_DELAY_IN_SECONDS;

            if (!isDockerInstalled[0]) {
                return false;
            }

            log.info("Wait for docker started...");

            List<String> consoleOutput =
                    CommandRunner.runQuietWithReturn(Commands.GET_PROCESSES_LIST);

            for (String task : consoleOutput) {
                if (task.contains(DOCKER_TASK_NAME)) {
                    thread.interrupt();
                    return true;
                }
            }

            if (timeSpent >= MAX_WAIT_TIME_SECONDS) {
                String errorMessage = "Timeout reached while trying to start docker";
                log.error(errorMessage);
                throw new InterruptedException(errorMessage);
            }
        }
    }

    private static boolean showOfferDockerInstallationPopUp() {
        JFrame jFrame = new JFrame();
        int result = JOptionPane.showConfirmDialog(jFrame,
                "Docker not installed on your computer. Would you like to install docker? (Docker is required)");
        return result == 0;
    }

    private static abstract class Commands {
        public static final String RUN_DOCKER = "\"C:\\Program Files\\Docker\\Docker\\Docker Desktop.exe\" & echo Successful";
        public static final String DOWNLOAD_DOCKER_DESKTOP =
                String.format("cd %s & wget %s", INSTALLER_PATH, DOCKER_DESKTOP_DOWNLOAD_LINK);
        public static final String DOCKER_QUIET_INSTALL =
                String.format("cd %s & \"%s\" install --quiet", INSTALLER_PATH, INSTALLER_FILE_NAME);
        public static final String DELETE_DOCKER_INSTALLER =
                String.format("cd %s & del \"%s\"", INSTALLER_PATH, INSTALLER_FILE_NAME);
        public static final String GET_PROCESSES_LIST = "tasklist.exe";
    }

}
