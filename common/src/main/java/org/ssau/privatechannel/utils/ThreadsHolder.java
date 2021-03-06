package org.ssau.privatechannel.utils;

import java.util.HashMap;
import java.util.Map;

public abstract class ThreadsHolder {
    private static final Map<String, Thread> RUNNING_THREADS = new HashMap<>();

    public static synchronized void addAndRunThread(String id, Thread thread) {
        thread.start();
        RUNNING_THREADS.put(id, thread);
    }

    public static synchronized void removeAndStopById(String id) {
        if (RUNNING_THREADS.containsKey(id)) {
            Thread thread = RUNNING_THREADS.get(id);
            thread.interrupt();
        }
    }
}
