package org.ssau.privatechannel.utils;

import java.io.IOException;
import java.util.List;

public class DbClusterInstaller {

    private static class Commands {
        public static final String GET_CONTAINER_INFO = "docker ps -f name=%s -a";
        public static final String START_CONTAINER_BY_ID = "docker start %s";
    }

    private static class Instances {
        public static final String SERVER = "server_db";
        public static final String CLIENT = "client_db";
    }

    private static class ContainerStatuses {
        public static final String EXITED = "Exited";
    }

    private static final Integer CLIENTS_COUNT = 2;
    private static final String START_PORT = "7430";

    public static void run() throws IOException {

        List<String> consoleOutput =
                CommandRunner.runWithReturn(String.format(Commands.GET_CONTAINER_INFO, Instances.SERVER));

        // Server db container exists
        if (consoleOutput.size() > 1) {
            String containerInfo = consoleOutput.get(1);
            runContainerIfStopped(containerInfo);
        } else {
            PostgresInstaller.run(Instances.SERVER, START_PORT);
        }

        String currentClientPort = String.valueOf(Integer.parseInt(START_PORT));
        for (int i = 0; i < CLIENTS_COUNT; ++i) {
            currentClientPort = String.valueOf(Integer.parseInt(currentClientPort) + 1);

            String currentInstance = String.format("%s_%s", Instances.CLIENT, i);
            consoleOutput = CommandRunner.runWithReturn(String.format(Commands.GET_CONTAINER_INFO, currentInstance));

            // Current client db container exists
            if (consoleOutput.size() > 1) {
                String containerInfo = consoleOutput.get(1);
                runContainerIfStopped(containerInfo);
            } else {
                PostgresInstaller.run(currentInstance, currentClientPort);
            }
        }
    }

    private static void runContainerIfStopped(String containerInfo) throws IOException {
        if (!containerInfo.contains(ContainerStatuses.EXITED)) {
            return;
        }

        String containerId = containerInfo.split(" ")[0];
        CommandRunner.run(String.format(Commands.START_CONTAINER_BY_ID, containerId));
    }
}
