package org.ssau.privatechannel.utils;

import lombok.extern.slf4j.Slf4j;
import org.ssau.privatechannel.exception.DockerMissingException;

import javax.swing.*;
import java.io.IOException;

@Slf4j
public class DockerInstaller {

    private static final String DOCKER_DESKTOP_DOWNLOAD_LINK =
            "https://desktop.docker.com/win/main/amd64/Docker%20Desktop%20Installer.exe";
    private static final String INSTALLER_PATH = "installation/docker/";
    private static final String INSTALLER_FILE_NAME = "Docker Desktop Installer.exe";

    private static abstract class Commands {
        public static final String RUN_DOCKER = "\"C:\\Program Files\\Docker\\Docker\\Docker Desktop.exe\" & echo Successful";
        public static final String DOWNLOAD_DOCKER_DESKTOP =
                String.format("cd %s & wget %s", INSTALLER_PATH, DOCKER_DESKTOP_DOWNLOAD_LINK);
        public static final String DOCKER_QUIET_INSTALL =
                String.format("cd %s & \"%s\" install --quiet", INSTALLER_PATH, INSTALLER_FILE_NAME);
        public static final String DELETE_DOCKER_INSTALLER =
                String.format("cd %s & del \"%s\"", INSTALLER_PATH, INSTALLER_FILE_NAME);
    }

    public static void run() throws IOException, InterruptedException, DockerMissingException {

        String result = CommandRunner.runWithReturn(Commands.RUN_DOCKER).get(0);

        // Docker not installed
        if (!result.equals("Successful")) {
            log.info("Docker Desktop not installed on current PC. Installation of docker desktop will be offer");
            boolean isDockerCanBeInstalled = showOfferDockerInstallationPopUp();

            if (isDockerCanBeInstalled) {
                log.info("Starting docker desktop installation...");
                CommandRunner.runQuiet(Commands.DOWNLOAD_DOCKER_DESKTOP); // Work
                CommandRunner.run(Commands.DOCKER_QUIET_INSTALL); // Work
                CommandRunner.run(Commands.DELETE_DOCKER_INSTALLER); // Work
            } else {
                log.error("Docker will not be installed on current PC: user cancelled installation. Exiting...");
                throw new DockerMissingException("Docker-Desktop is required for private-channel application");
            }
        }
    }

    private static boolean showOfferDockerInstallationPopUp() {
        JFrame jFrame = new JFrame();
        int result = JOptionPane.showConfirmDialog(jFrame,
                "Docker not installed on your computer. Would you like to install docker? (Docker is required)");
        return result == 0;
    }

}
