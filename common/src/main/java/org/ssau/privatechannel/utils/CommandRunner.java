package org.ssau.privatechannel.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CommandRunner {
    public static void run(String command) throws IOException {
        runWithReturn(command);
    }

    public static List<String> runWithReturn(String command) throws IOException {
        ProcessBuilder builder = new ProcessBuilder(
                "cmd.exe", "/c", command);
        builder.redirectErrorStream(true);
        Process process = builder.start();

        InputStreamReader inputStreamReader = new InputStreamReader(process.getInputStream());
        BufferedReader r = new BufferedReader(inputStreamReader);

        List<String> fullConsoleOutput = new ArrayList<>();
        String line;

        while (true) {
            line = r.readLine();
            if (line == null || "".equals(line)) { break; }
            fullConsoleOutput.add(line);
            System.out.println(line);
        }
        return fullConsoleOutput;
    }

    public static void runQuiet(String command) throws IOException {
        ProcessBuilder builder = new ProcessBuilder(
                "cmd.exe", "/c", command);
        builder.redirectErrorStream(true);
        Process process = builder.start();

        InputStreamReader inputStreamReader = new InputStreamReader(process.getInputStream());
        BufferedReader r = new BufferedReader(inputStreamReader);

        String line;

        while (true) {
            line = r.readLine();
            if (line == null) { break; }
        }
    }
}
