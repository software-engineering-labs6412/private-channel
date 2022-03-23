package org.ssau.privatechannel.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.relational.core.sql.In;
import org.ssau.privatechannel.constants.SystemProperties;
import org.ssau.privatechannel.exception.DockerMissingException;
import org.ssau.privatechannel.exception.InvalidAppInstallationModeException;
import org.ssau.privatechannel.exception.InvalidInstanceTypeException;

import javax.swing.*;
import java.io.IOException;
import static org.ssau.privatechannel.utils.DbClusterInstaller.Instances;

@Slf4j
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

    public static void run(String mode, String instance) throws IOException, InterruptedException,
            DockerMissingException, InvalidAppInstallationModeException, InvalidInstanceTypeException {
        DockerInstaller.run();

        // TODO (tmp): need to be deleted
        mode = debugModeDialogWindow();

        System.setProperty(SystemProperties.DB_URL, DefaultDbParams.DEFAULT_DB_URL);
        System.setProperty(SystemProperties.DB_USER, DefaultDbParams.DEFAULT_DB_USERNAME);
        System.setProperty(SystemProperties.DB_PASSWORD, DefaultDbParams.DEFAULT_DB_PASSWORD);

        if (Mode.CLUSTER_DB.equals(mode)) {
            log.info("Full db cluster will be installed");
            DbClusterInstaller.run();
        }
        else if (Mode.SINGLE_DB.equals(mode)) {
            log.info("Single database will be installed");
            if (Instances.SERVER.equals(instance) || Instances.CLIENT.equals(instance)) {
                DbClusterInstaller.singleInstall(instance);
            } else {
                log.error("Invalid instance type provided during database deployment: {}. Must be {} or {}",
                        instance, Instances.SERVER, Instances.CLIENT);
                throw new InvalidInstanceTypeException("Invalid instance type provided during application installation");
            }
        } else {
            log.error("Provided invalid application installation mode: {}. Must be {} or {}",
                    mode, Mode.CLUSTER_DB, Mode.SINGLE_DB);
            throw new InvalidAppInstallationModeException("Invalid application installation mode");
        }

    }

    @Deprecated // TODO (tmp): must be deleted before release!
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
