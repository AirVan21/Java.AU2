package ru.spbau.javacourse.torrent.tracker;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Tracker class
 */
public class Tracker {
    private ServerSocket socket;
    private volatile boolean isStopped;

    public  Tracker() {}

    public synchronized void start(int port) throws IOException {
        if (socket != null) {
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
        while (!isStopped && !socket.isClosed()) {
            try {
                acceptTask();
            } catch (IOException e) {
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
