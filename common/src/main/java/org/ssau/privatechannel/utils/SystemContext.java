package org.ssau.privatechannel.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class SystemContext {

    public static final Map<String, String> context = new HashMap<>();

    public static void setProperty(String propertyName, String value) {
        context.put(propertyName, value);
    }

    public static String getProperty(String propertyName) {
        return context.get(propertyName);
    }

    public static void printAllProperties() {
        for (Map.Entry<String, String> property : context.entrySet()) {
            String prop = String.format("Property [%s] = [%s]", property.getKey(), property.getValue());
            log.info(prop);
        }
    }
}
