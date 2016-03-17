package com.freelancer;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Main class of udprelay program. This class parses user input and starts
 * udprelay.
 */
public class Main {
    public static void main(String[] args) {
        if (args.length != 4) {
            printUsageMessage();
            return;
        }

        int server_listen_port;
        int client_listen_port;
        int server_port;
        InetAddress server_address;

        try {
            server_address = InetAddress.getByName(args[1]);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.err.println("Unknown host name or wrong IP address provided!");
            throw new RuntimeException("Unknown host name or wrong IP address provided!");
        }
        server_port = parsePortNumber(args[0], "server port");
        client_listen_port = parsePortNumber(args[2], "relay client listening port");
        server_listen_port = parsePortNumber(args[3], "relay server listening port");

        UDPRelay UDPRelay = new UDPRelay(
                server_address, server_port, client_listen_port, server_listen_port);
        UDPRelay.run();
    }

    /**
     * Prints usage message on the STDOUT.
     */
    private static void printUsageMessage() {
        String usage_message = "Usage:" + System.lineSeparator() +
                "java com.freelancer.Main <server port> " +
                "<server IP> <relay client port> <relay server port>" +
                System.lineSeparator() +
                "\t<server port> - server port, on which it is listening" +
                System.lineSeparator() +
                "\t<server IP> - server IP address" +
                System.lineSeparator() +
                "\t<relay client port> - relay client listening port" +
                System.lineSeparator() +
                "\t<relay server port> - relay server listening port";
        System.out.println(usage_message);
    }

    /**
     * Parses string with port number. If string malformed, then
     * {@code RuntimeException} will be returned.
     * @param port String representation of the port number.
     * @param port_name Port name - used in the error messages.
     * @return Port number as integer value.
     */
    private static int parsePortNumber(String port, String port_name) {
        try {
            int result;
            result = Integer.valueOf(port);
            if (result < 0 || result > 65535) {
                System.err.println("Given port number out of range [0:65535]");
                throw new RuntimeException("port number out of range");
            }
            return result;
        } catch (NumberFormatException e) {
            System.err.println("Cannot parse " + port_name + "!");
            System.err.println("It is not a number: " + port);
            throw new RuntimeException(e);
        }
    }
}
