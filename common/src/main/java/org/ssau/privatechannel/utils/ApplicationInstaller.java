package org.ssau.privatechannel.utils;

import java.io.IOException;

public class ApplicationInstaller {
    public static void run() throws IOException, InterruptedException {
        DockerInstaller.run();
        DbClusterInstaller.run();
        // TODO: Application deployment
    }
}
