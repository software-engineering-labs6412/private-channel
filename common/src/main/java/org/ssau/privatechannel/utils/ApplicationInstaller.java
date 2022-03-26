package org.ssau.privatechannel.utils;

import lombok.extern.slf4j.Slf4j;
import org.ssau.privatechannel.constants.SystemProperties;
import org.ssau.privatechannel.exception.DockerMissingException;
import org.ssau.privatechannel.exception.InvalidAppInstallationModeException;
import org.ssau.privatechannel.exception.InvalidInstanceTypeException;

import javax.swing.*;
import java.io.IOException;

import static org.ssau.privatechannel.utils.DbClusterInstaller.Instances;

@Slf4j
public class ApplicationInstaller {

    public static void run(String instance) throws IOException, InterruptedException,
            DockerMissingException, InvalidAppInstallationModeException, InvalidInstanceTypeException {
        DockerInstaller.run();

        System.setProperty(SystemProperties.DB_URL, DefaultDbParams.DEFAULT_DB_URL);
        System.setProperty(SystemProperties.DB_USER, DefaultDbParams.DEFAULT_DB_USERNAME);
        System.setProperty(SystemProperties.DB_PASSWORD, DefaultDbParams.DEFAULT_DB_PASSWORD);

        log.info("Single database will be installed");
        if (Instances.SERVER.equals(instance) || Instances.CLIENT.equals(instance)) {
            DbClusterInstaller.singleInstall(instance);
        } else {
            log.error("Invalid instance type provided during database deployment: {}. Must be {} or {}",
                    instance, Instances.SERVER, Instances.CLIENT);
            throw new InvalidInstanceTypeException("Invalid instance type provided during application installation");
        }
    }

    public static abstract class Mode {
        public static final String CLUSTER_DB = "cluster_db";
        public static final String SINGLE_DB = "single_db";
    }


    private static abstract class DefaultDbParams {
        public static final String DEFAULT_DB_URL = "jdbc:postgresql://localhost:%s/private_channel";
        public static final String DEFAULT_DB_USERNAME = "postgres";
        public static final String DEFAULT_DB_PASSWORD = "postgres";
    }
}
