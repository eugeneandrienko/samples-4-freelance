package com.freelancer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * This class implements udprelay main functionality.
 * <p>
 * Our relay works with the next assumptions:
 * <ul>
 * <li>Cycle of data exchange between client and server is strict and
 * never be violated.</li>
 * <li>Data exchange always start with only one message from client</li>
 * <li>Server sends only one message to client, only when it received
 * client's message.</li>
 * <li>Data exchange never be started without message sent from client to server.</li>
 * </ul>
 * <p>
 * If client or server send more than one message (packet) to each other - only
 * first message (packet) will be passed to the other side. Other packet(s) will
 * be sent to other side on the next cycle(s) of data exchange.
 * <p>
 * Typical cycle of data exchange should looks like this (as described in
 * assignment):
 * <ol>
 * <li>Server waiting for message from client.</li>
 * <li>Client send only one message to server and starts waiting for response</li>
 * <li>Server receives message from client and sends answer to it.</li>
 * <li>Client receives message.</li>
 * </ol>
 * <p>
 * In the client-server data transmission line our relay works as described below:
 * <ul>
 * <li>Relay has port for client connection and data. Client should connect
 * to this port instead of server's port.</li>
 * <li>Relay has port, which is uses to send data to server and receive data
 * from server. Server's address and port should be provided by user.</li>
 * <li>When relay receive packet from client - it simply sends it to server,
 * using corresponding relay's port.</li>
 * <li>When relay receive packet from server - it sends it to client.
 * Client's address and port obtained from client's packet, which is sent at
 * the start of data exchange cycle.</li>
 * </ul>
 */
public class UDPRelay {
    /**
     * @param server_address     Server address
     * @param server_port        Server port
     * @param client_listen_port Relay's port, on which relay will listen for
     *                           data from client.
     * @param server_listen_port Relay's port, on which relay will listen for
     *                           data from server.
     */
    public UDPRelay(
            InetAddress server_address,
            int server_port,
            int client_listen_port,
            int server_listen_port) {
        try {
            clientSideSocket = new DatagramSocket(client_listen_port);
            serverSideSocket = new DatagramSocket(server_listen_port);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
        serverAddress = server_address;
        serverPort = server_port;
    }

    /**
     * Executes main cycle of udprelay, which implements data exchange cycle,
     * described above.
     */
    public void run() {
        final int BUFFER_LENGTH = 1024; // 1K. UDP datagrams cannot be larger than 64K!
        byte[] buffer = new byte[BUFFER_LENGTH];

        while (true) {
            // Takes a message from udpclient at one socket.
            DatagramPacket packet_from_client = new DatagramPacket(buffer, buffer.length);
            try {
                clientSideSocket.receive(packet_from_client);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            // Forwards it to udpserver using the 2nd socket.
            DatagramPacket packet_to_server = new DatagramPacket(
                    buffer, buffer.length, serverAddress, serverPort);
            try {
                serverSideSocket.send(packet_to_server);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            // Waits for udpserver reply using the 2nd socket.
            DatagramPacket packet_from_server = new DatagramPacket(buffer, buffer.length);
            try {
                serverSideSocket.receive(packet_from_server);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            // Send reply to udpclient using the 1st socket.
            DatagramPacket packet_to_client = new DatagramPacket(buffer, buffer.length,
                    packet_from_client.getAddress(), packet_from_client.getPort());
            try {
                clientSideSocket.send(packet_to_client);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private DatagramSocket clientSideSocket;
    private DatagramSocket serverSideSocket;
    private InetAddress serverAddress;
    private int serverPort;
}
