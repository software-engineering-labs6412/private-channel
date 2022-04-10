package org.ssau.privatechannel.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.ssau.privatechannel.utils.CommandRunner;

import java.io.IOException;

@Slf4j
@Service
public class NetworkAdapterService {

    public void enableInterfaces(String netInterface) throws IOException {
        String command = String.format(Commands.ENABLE_INTERFACE, netInterface);
        CommandRunner.runQuiet(command);
        log.info(String.format("%s interface now enabled", netInterface));
    }

    public void disableInterfaces(String netInterface) throws IOException {
        String command = String.format(Commands.DISABLE_INTERFACE, netInterface);
        CommandRunner.runQuiet(command);
        log.info(String.format("\"%s\" interface now disabled", netInterface));
    }

    private static abstract class Commands {
        public static final String
                DISABLE_INTERFACE = "netsh interface set interface \"%s\" disable",
                ENABLE_INTERFACE = "netsh interface set interface \"%s\" enable";
    }
}
