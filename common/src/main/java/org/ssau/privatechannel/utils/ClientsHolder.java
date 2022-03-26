package org.ssau.privatechannel.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClientsHolder {
    private static final List<String> CLIENTS_IPS = new ArrayList<>();

    public static void addAllClients(String[] clients) {
        List<String> newClients = Arrays.asList(clients);
        CLIENTS_IPS.addAll(newClients);
    }

    public static List<String> getAllClients() {
        return CLIENTS_IPS;
    }
}
