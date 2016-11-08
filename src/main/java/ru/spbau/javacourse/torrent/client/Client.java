package ru.spbau.javacourse.torrent.client;


import ru.spbau.javacourse.torrent.utils.GlobalLogger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Client class - implementation of client logic.
 */
public class Client {
    private final String hostName;
    private final int port;
    private DataInputStream input;
    private DataOutputStream output;
    private Socket socket;

    public Client(String hostName, int port) {
        this.hostName = hostName;
        this.port = port;
    }

    /**
     * Opens connection
     * @throws IOException
     */
    public synchronized void connect() throws IOException {
        if (socket != null) {
            GlobalLogger.log(getClass().getName(), "Can't reconnect!");
            return;
        }

        socket = new Socket(hostName, port);
        input = new DataInputStream(socket.getInputStream());
        output = new DataOutputStream(socket.getOutputStream());
    }

    /**
     * Closes connection
     * @throws IOException
     */
    public synchronized void disconnect() throws IOException {
        if (socket == null) {
            GlobalLogger.log(getClass().getName(), "Connection is not found!");
            return;
        }

        input.close();
        output.close();
        socket.close();
    }
}
