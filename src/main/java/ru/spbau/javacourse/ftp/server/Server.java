package ru.spbau.javacourse.ftp.server;

import lombok.extern.java.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Server class - class which handles Client requests and resolves connections
 */
@Log
public class Server {
    private ServerSocket socket;
    private volatile boolean isStopped;

    public Server() {}

    /**
     * Starts server
     * @param port - port for server socket
     * @throws IOException
     */
    public synchronized void start(int port) throws IOException {
        if (socket != null) {
            log.info("Server is already up!");
            return;
        }

        socket = new ServerSocket(port);
        isStopped = false;
        new Thread(this::handle).start();
    }

    /**
     * Stops server
     * @throws IOException
     * @throws InterruptedException
     */
    public synchronized void stop() throws IOException, InterruptedException {
        if (socket == null) {
            log.info("Couldn't stop empty socket!");
            return;
        }

        isStopped = true;
        socket.close();
        wait();
    }

    /**
     * Listens connections
     */
    private void handle() {
        while (!isStopped) {
            try {
                acceptTask();
            } catch (IOException e) {
                log.info("Couldn't accept task!");
            }

            synchronized (this) {
                notify();
            }
        }
    }

    /**
     * Accepts connection to server socket
     * @throws IOException
     */
    private void acceptTask() throws IOException {
        final Socket connection;
        try {
            connection = socket.accept();
        } catch (IOException e) {
            return;
        }

        final HandleTask task = new HandleTask(connection);
        new Thread(task).start();
    }
}


