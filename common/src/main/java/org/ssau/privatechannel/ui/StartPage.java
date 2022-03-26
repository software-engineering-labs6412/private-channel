package org.ssau.privatechannel.ui;

import lombok.extern.slf4j.Slf4j;
import org.ssau.privatechannel.constants.SystemProperties;
import org.ssau.privatechannel.exception.ValidationException;
import org.ssau.privatechannel.service.IpService;
import org.ssau.privatechannel.utils.ClientsHolder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class StartPage {

    private static abstract class DefaultParams {

        // Window settings
        public static final Dimension WINDOW_SIZE = new Dimension(320, 320);
        public static final Point LOCATION_POINT = new Point(100, 100);

        // Grid settings
        public static final Integer ROWS = 11;
        public static final Integer COLUMNS = 2;
        public static final Integer H_GAP = 8;
        public static final Integer V_GAP = 5;

        // Validation constants
        public static final int MAX_PARAMETER_LENGTH = 50;
        public static final int MIN_PORT = 7000;
        public static final int MAX_PORT = 9000;

        // Default params
        public static final String DEFAULT_APP_PORT = "8080";
    }

    private static abstract class Instances {
        public static final String CLIENT = "Client";
        public static final String SERVER = "Server";
    }

    public static void show() throws IOException {

        JDialog settingsWindow = new JDialog(new JFrame(), "Application settings", true);
        settingsWindow.setSize(DefaultParams.WINDOW_SIZE);
        settingsWindow.setLocation(DefaultParams.LOCATION_POINT);

        settingsWindow.addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        // Вспомогательная панель
        JPanel grid = new JPanel();

        GridLayout layout;

        if (System.getProperty(SystemProperties.INSTANCE).equals(Instances.CLIENT)) {
            layout = new GridLayout(DefaultParams.ROWS,
                    DefaultParams.COLUMNS,
                    DefaultParams.H_GAP,
                    DefaultParams.V_GAP);
        }
        else {
            layout = new GridLayout(DefaultParams.ROWS-2,
                    DefaultParams.COLUMNS,
                    DefaultParams.H_GAP,
                    DefaultParams.V_GAP);
        }

        grid.setLayout(layout);

        String[] instances = {System.getProperty(SystemProperties.INSTANCE)};

        IpService ipService = new IpService();
        Map<String, String> allInternalRegisteredIp = ipService.getAllInternalRegisteredIp();
        List<String> interface2Ip = new ArrayList<>();

        for (Map.Entry<String, String> currentPair : allInternalRegisteredIp.entrySet()) {
            interface2Ip.add(currentPair.getKey() + "=(" + currentPair.getValue() + ")");
        }

        String[] interface2IpArray = interface2Ip.toArray(String[]::new);

        JComboBox<String> instancesComboBox = new JComboBox<>(instances);
        instancesComboBox.setEditable(false);
        JComboBox<String> interfacesComboBox = new JComboBox<>(interface2IpArray);

        // Selecting instance
        grid.add(new JLabel("Select instance:"));
        grid.add(instancesComboBox);

        // Setup application port
        grid.add(new JLabel("Application port"));
        JTextField appPort = new JTextField(DefaultParams.DEFAULT_APP_PORT);
        grid.add(appPort);

        // DB settings
        grid.add(new JLabel("PG instance name:"));
        JTextField dbInstanceName = new JTextField("private_channel");
        grid.add(dbInstanceName);
        grid.add(new JLabel("PG username:"));
        JTextField pgUser = new JTextField("postgres");
        grid.add(pgUser);
        grid.add(new JLabel("PG password:"));
        JTextField pgPassword = new JTextField("postgres");
        grid.add(pgPassword);
        grid.add(new JLabel("PG instance port:"));
        JTextField dbPort = new JTextField("7430");
        grid.add(dbPort);
        grid.add(new JLabel("Main database:"));
        JTextField mainDatabase = new JTextField("private_channel");
        grid.add(mainDatabase);

        // Network interface
        grid.add(new JLabel("Using network interface:"));
        grid.add(interfacesComboBox);

        JTextField serverIp = new JTextField();
        JTextField receiverIp = new JTextField();

        // Server and other client IPs (only for clients)
        if (instances[0].equals(Instances.CLIENT)) {
            grid.add(new JLabel("Server ip:"));
            grid.add(serverIp);
            grid.add(new JLabel("Receiver ip:"));
            grid.add(receiverIp);
        }

        JButton startButton = new JButton("Start app");
        grid.add(startButton);

        // =====================================================
        JScrollPane clientsIps = null;
        JTextField currentClientIp = new JTextField();
        JTextArea clientsIpsTextArea = new JTextArea();
        currentClientIp.setToolTipText("Write here IP of client...");
        currentClientIp.setColumns(15);
        if (instances[0].equals(Instances.SERVER)) {

            JPanel clientsPanel = new JPanel();
            GridBagLayout clientsLayout = new GridBagLayout();

            clientsPanel.setLayout(clientsLayout);

            clientsIpsTextArea.setRows(10);
            clientsIpsTextArea.setColumns(10);
            clientsIps = new JScrollPane(clientsIpsTextArea,
                    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            clientsIpsTextArea.setEditable(false);
            clientsPanel.add(new JLabel("Clients IPs for connection  "));
            clientsPanel.add(clientsIps);
            clientsPanel.add(currentClientIp);

            JButton addClientButton = new JButton("Add current ip as client");
            Set<String> uniqueIps = new HashSet<>();
            ActionListener listener = ae -> {
                String currentIp = currentClientIp.getText();

                if (uniqueIps.contains(currentIp)) {
                    return;
                }

                String[] ipAndPort = currentIp.split(":");
                if (ipAndPort.length != 2) {
                    String errorMessage = "Invalid client address: " + currentIp;
                    showErrorDialog(errorMessage);
                    throw new RuntimeException(errorMessage);
                }

                try {
                    validateIp("Client IP", ipAndPort[0]);
                } catch (ValidationException e) {
                    String errorMessage = "Invalid client ip address: " + ipAndPort[0];
                    showErrorDialog(errorMessage);
                    throw new RuntimeException(errorMessage);
                }

                try {
                    validatePort(ipAndPort[1]);
                } catch (ValidationException e) {
                    String errorMessage = "Invalid port for client address: " + ipAndPort[1];
                    showErrorDialog(errorMessage);
                    throw new RuntimeException(errorMessage);
                }

                uniqueIps.add(currentIp);

                String currentText = clientsIpsTextArea.getText();
                clientsIpsTextArea.setText(String.format("%s\n%s", currentText, currentClientIp.getText()));
            };
            addClientButton.addActionListener(listener);
            clientsPanel.add(addClientButton);

            JTabbedPane pane = new JTabbedPane();
            pane.addTab("Main settings", grid);
            pane.addTab("Clients", clientsPanel);
            settingsWindow.getContentPane().add(pane);
        } else {
            settingsWindow.getContentPane().add(grid);
        }

        settingsWindow.pack();

        ActionListener listener = ae -> {

            // Environment setup...
            String applicationPort = appPort.getText();
            String databaseInstanceName = dbInstanceName.getText();
            String postgresUser = pgUser.getText();
            String postgresPassword = pgPassword.getText();
            String postgresPort = dbPort.getText();
            String postgresDb = mainDatabase.getText();
            String network = Objects.requireNonNull(interfacesComboBox.getSelectedItem()).toString();
            String serverIpAddress;
            String receiverIpAddress;

            if (instances[0].equals(Instances.CLIENT)) {
                serverIpAddress = serverIp.getText();
                receiverIpAddress = receiverIp.getText();

                try {
                    validateIp(SystemProperties.SERVER_IP, serverIpAddress);
                    validateIp(SystemProperties.RECEIVER_IP, receiverIpAddress);
                } catch (ValidationException e) {
                    throw new RuntimeException(e);
                }

                System.setProperty(SystemProperties.SERVER_IP, serverIpAddress);
                System.setProperty(SystemProperties.RECEIVER_IP, receiverIpAddress);
            } else {
                try {
                    String clients = clientsIpsTextArea.getText().substring(1);
                    ClientsHolder.addAllClients(clients.split("\n"));
                }
                catch (Exception e) {
                    String errorMessage =
                            "Something wrong during parsing clients ips. " +
                                    "May be you forgot to add at least one client IP?";
                    showErrorDialog(errorMessage);
                    throw e;
                }
            }

            try {
                validatePort(applicationPort);
                validateParameter(SystemProperties.DB_INSTANCE, databaseInstanceName);
                validateParameter(SystemProperties.DB_USER, postgresUser);
                validateParameter(SystemProperties.DB_PASSWORD, postgresPassword);
                validatePort(postgresPort);
                validateParameter(SystemProperties.MAIN_DB, postgresDb);
            } catch (ValidationException e) {
                throw new RuntimeException(e);
            }

            System.setProperty(SystemProperties.APP_PORT, applicationPort);
            System.setProperty(SystemProperties.DB_INSTANCE, databaseInstanceName);
            System.setProperty(SystemProperties.DB_USER, postgresUser);
            System.setProperty(SystemProperties.DB_PASSWORD, postgresPassword);
            System.setProperty(SystemProperties.DB_PORT, postgresPort);
            System.setProperty(SystemProperties.MAIN_DB, postgresDb);

            System.setProperty(SystemProperties.NETWORK, network.split("=")[0]);

            Matcher matcher = Pattern.compile("[0-9]+.[0-9]+.[0-9]+.[0-9]").matcher(network);
            boolean isNetworkProvided = matcher.find();

            if (isNetworkProvided) {
                String currentIp = matcher.group();

                try {
                    validateIp(SystemProperties.CURRENT_IP, currentIp);
                } catch (ValidationException e) {
                    throw new RuntimeException(e);
                }

                System.setProperty(SystemProperties.CURRENT_IP, currentIp);
            }

            settingsWindow.setVisible(false);
        };

        startButton.addActionListener(listener);
        settingsWindow.setVisible(true);
    }

    private static void validateParameter(String parameterName, String parameter) throws ValidationException {
        if (parameter.isBlank() || parameter.isEmpty()) {
            String errorMessage = String.format("Parameter %s not specified", parameterName);
            showErrorDialog(errorMessage);
            throw new ValidationException(errorMessage);
        }
        if (parameter.length() > DefaultParams.MAX_PARAMETER_LENGTH) {
            String errorMessage = String.format("Parameter is too long. Must be not bigger than %s",
                    DefaultParams.MAX_PARAMETER_LENGTH);
            showErrorDialog(errorMessage);
            throw new ValidationException(errorMessage);
        }
    }

    private static void validateIp(String parameterName, String ip) throws ValidationException {

        validateParameter(parameterName, ip);

        String[] parts = ip.split("\\.");

        if (parts.length == 0) {
            String errorMessage =
                    String.format("Invalid ip structure. Must be [0-255].[0-255].[0-255].[0-255]. Got: %s", ip);
            showErrorDialog(errorMessage);
            throw new ValidationException(errorMessage);
        }

        try {
            for (String part : parts) {
                int parsed = Integer.parseInt(part);
                if (parsed < 0 || parsed > 255) {
                    String errorMessage =
                            String.format("Invalid part of ip address. Got ip: %s; Invalid part: %s", ip, part);
                    showErrorDialog(errorMessage);
                    throw new ValidationException(errorMessage);
                }
            }
        }
        catch (NumberFormatException e) {
            String errorMessage =
                    String.format("Invalid part of ip address. Ip must include only digits and dots. Got ip: %s", ip);
            showErrorDialog(errorMessage);
            throw new ValidationException(errorMessage);
        }
    }

    private static void validatePort(String port) throws ValidationException {

        validateParameter(SystemProperties.DB_PORT, port);

        try {
            int parsed = Integer.parseInt(port);
            if (parsed < DefaultParams.MIN_PORT || parsed > DefaultParams.MAX_PORT) {
                String errorMessage = String.format("Invalid port. Must be in range [%s-%s]",
                                DefaultParams.MIN_PORT, DefaultParams.MAX_PORT);
                showErrorDialog(errorMessage);
                throw new ValidationException(errorMessage);
            }
        } catch (NumberFormatException e) {
            String errorMessage = String.format("Invalid port. Port must include only digits. Got: %s", port);
            showErrorDialog(errorMessage);
            throw new ValidationException(errorMessage);
        }
    }

    private static void showErrorDialog(String errorMessage) {
        log.error(errorMessage);
        JFrame jFrame = new JFrame();
        JOptionPane.showMessageDialog(jFrame, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
    }
}