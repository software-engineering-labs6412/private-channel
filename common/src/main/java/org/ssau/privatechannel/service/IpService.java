package org.ssau.privatechannel.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.ssau.privatechannel.utils.CommandRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class IpService {

    public void enableFirewall() throws IOException {
        CommandRunner.run(Commands.ENABLE_FIREWALL);
        log.info("Firewall enabled");
    }

    public Map<String, String> getAllInternalRegisteredIp() throws IOException {
        Map<String, String> result = new HashMap<>();

        List<String> consoleOutput = CommandRunner.runQuietWithReturn(Commands.GET_ALL_INTERFACES_INFO);
        List<String> interfaces = new ArrayList<>();
        Pattern adapterNamePattern = Pattern.compile("[^.]+[A-z\\d-() ]+:");
        for (String resString : consoleOutput) {

            boolean isInterfaceFound;
            if (resString.matches(adapterNamePattern.pattern()) && !resString.isEmpty()) {
                Matcher matcher = Pattern.compile("[A-z0-9-() ]+").matcher(resString);
                isInterfaceFound = matcher.find();

                if (isInterfaceFound && !matcher.group().equals(" ")) {
                    interfaces.add(matcher.group());
                }
            }

            Matcher ipv4Matcher = Pattern.compile("IPv4-").matcher(resString);
            if (ipv4Matcher.find()) {
                Matcher matcher = Pattern.compile("[0-9]+.[0-9]+.[0-9]+.[0-9]+").matcher(resString);
                boolean isIpV4Found = matcher.find();

                if (isIpV4Found) {
                    result.put(interfaces.get(interfaces.size() - 1), matcher.group());
                }
            }
        }

        return result;
    }

    public void blockIP(IpAddress ipAddress, String ruleName) throws IOException {
        String command = String.format(Commands.BLOCK_IP_ADDRESS, ruleName, Ports.HTTP, ipAddress.getIp());
        CommandRunner.run(command);
        log.info(String.format("IP address \"%s\" blocked with rule name = \"%s\"", ipAddress.getIp(), ruleName));
    }

    public void blockHttpPort(String ruleName) throws IOException {
        String command = String.format(Commands.BLOCK_HTTP_PORT, ruleName, Ports.HTTP);
        CommandRunner.run(command);
        log.info(String.format("Http port \"%s\" blocked with rule name = \"%s\"", Ports.HTTP, ruleName));
    }

    public void deleteRuleByName(String ruleName) throws IOException {
        String command = String.format(Commands.DELETE_RULE, ruleName);
        CommandRunner.run(command);
        log.info(String.format("Rule name \"%s\" deleted", ruleName));
    }

    @Data
    @AllArgsConstructor
    public static class IpAddress {
        private String ip;
    }

    private static abstract class Commands {
        public static final String
                GET_ALL_INTERFACES_INFO = "ipconfig",
                ENABLE_FIREWALL = "netsh advfirewall set allprofiles state off",
                BLOCK_IP_ADDRESS = "netsh advfirewall firewall add rule name=\"%s\" protocol=TCP "
                        + "localport=%s action=block dir=IN remoteip=%s",
                BLOCK_HTTP_PORT =
                        "netsh advfirewall firewall add rule name=\"%s\" protocol=TCP localport=%s action=block dir=IN",
                DELETE_RULE =
                        "netsh advfirewall firewall delete rule name=\"%s\"";
    }

    private static abstract class Ports {
        public static final String
                HTTP = "8080";
    }
}
