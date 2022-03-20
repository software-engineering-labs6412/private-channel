package org.ssau.privatechannel.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;
import org.ssau.privatechannel.utils.CommandRunner;

import java.io.IOException;

@Service
public class IpService {

    @Data
    @AllArgsConstructor
    public static class IpAddress {
        private String ip;
        private String mask;
    }

    private static abstract class Commands {
        public static final String
                ENABLE_FIREWALL = "netsh advfirewall set allprofiles state off",
                DISABLE_FIREWALL = "netsh advfirewall set allprofiles state on",
                BLOCK_IP_ADDRESS = "netsh advfirewall firewall add rule name=\"%s\" protocol=TCP "
                        + "localport=%s action=block dir=IN remoteip=%s",
                UNBLOCK_IP_ADDRESS = "netsh advfirewall firewall add rule name=\"%s\" protocol=TCP "
                        + "localport=%s action=allow dir=IN remoteip=%s",
                BLOCK_HTTP_PORT =
                        "netsh advfirewall firewall add rule name=\"%s\" protocol=TCP localport=%s action=block dir=IN",
                UNBLOCK_HTTP_PORT =
                        "netsh advfirewall firewall add rule name=\"%s\" protocol=TCP localport=%s action=allow dir=IN",
                DELETE_RULE =
                        "netsh advfirewall firewall delete rule name=\"%s\"";
    }

    private static abstract class Ports {
        public static final String
                HTTP = "8080";
    }

    public void enableFirewall() throws IOException {
        CommandRunner.run(Commands.ENABLE_FIREWALL);
    }

    public void disableFirewall() throws IOException {
        CommandRunner.run(Commands.DISABLE_FIREWALL);
    }

    public void blockIP(IpAddress ipAddress, String ruleName) throws IOException {
        String command = String.format(Commands.BLOCK_IP_ADDRESS, ruleName, Ports.HTTP, ipAddress.getIp());
        CommandRunner.run(command);
    }

    public void unblockIP(IpAddress ipAddress, String ruleName) throws IOException {
        String command = String.format(Commands.UNBLOCK_IP_ADDRESS, ruleName, Ports.HTTP, ipAddress.getIp());
        CommandRunner.run(command);
    }


    public void blockHttpPort(String ruleName) throws IOException {
        String command = String.format(Commands.BLOCK_HTTP_PORT, ruleName, Ports.HTTP);
        CommandRunner.run(command);
    }

    public void unblockHttpPort(String ruleName) throws IOException {
        String command = String.format(Commands.UNBLOCK_HTTP_PORT, ruleName, Ports.HTTP);
        CommandRunner.run(command);
    }

    public void deleteRuleByName(String ruleName) throws IOException {
        String command = String.format(Commands.DELETE_RULE, ruleName);
        CommandRunner.run(command);
    }
}
