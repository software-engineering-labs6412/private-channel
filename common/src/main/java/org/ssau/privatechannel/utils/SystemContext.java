package org.ssau.privatechannel.utils;

import java.util.HashMap;
import java.util.Map;

public class SystemContext {

    public static final Map<String, String> context = new HashMap<>();

    public static void setProperty(String propertyName, String value) {
        context.put(propertyName, value);
    }

    public static String getProperty(String propertyName) {
        return context.get(propertyName);
    }
}
