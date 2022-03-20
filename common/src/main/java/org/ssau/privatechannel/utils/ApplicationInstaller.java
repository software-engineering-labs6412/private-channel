package org.ssau.privatechannel.utils;

import org.ssau.privatechannel.constants.SystemProperties;

import javax.swing.*;
import java.io.IOException;
import static org.ssau.privatechannel.utils.DbClusterInstaller.Instances;

public class ApplicationInstaller {

    public static abstract class Mode {
        public static final String CLUSTER_DB = "cluster_db";
        public static final String SINGLE_DB = "single_db";
    }

    private static abstract class DefaultDbParams {
        public static final String DEFAULT_DB_URL = "jdbc:postgresql://localhost:%s/private_channel";
        public static final String DEFAULT_DB_USERNAME = "postgres";
        public static final String DEFAULT_DB_PASSWORD = "postgres";
    }

    public static void run(String mode, String instance) throws IOException, InterruptedException {
        DockerInstaller.run();

        // TODO: need to be deleted
        mode = debugModeDialogWindow();

        System.setProperty(SystemProperties.DB_URL, DefaultDbParams.DEFAULT_DB_URL);
        System.setProperty(SystemProperties.DB_USER, DefaultDbParams.DEFAULT_DB_USERNAME);
        System.setProperty(SystemProperties.DB_PASSWORD, DefaultDbParams.DEFAULT_DB_PASSWORD);

        if (Mode.CLUSTER_DB.equals(mode)) {
            DbClusterInstaller.run();
        }
        else if (Mode.SINGLE_DB.equals(mode)) {
            if (Instances.SERVER.equals(instance) || Instances.CLIENT.equals(instance)) {
                DbClusterInstaller.singleInstall(instance);
            } else {
                throw new RuntimeException("Invalid instance type provided during application installation");
            }
        } else {
            throw new RuntimeException("Invalid application installation mode");
        }

    }

    @Deprecated // TODO: must be deleted before release!
    private static String debugModeDialogWindow() {
        JFrame jFrame = new JFrame();
        int result = JOptionPane.showConfirmDialog(jFrame,
                "Click YES/OK for installing 3 databases (server, client1 and client2). " +
                        "Or click No for installing only related DB instance (server/client)");

        if (result == 0) {
            return Mode.CLUSTER_DB;
        }
        return Mode.SINGLE_DB;
    }
}
