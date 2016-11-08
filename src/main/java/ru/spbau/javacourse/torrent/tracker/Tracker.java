package ru.spbau.javacourse.torrent.tracker;

import ru.spbau.javacourse.torrent.utils.GlobalLogger;

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
            GlobalLogger.log(getClass().getName(), "");
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
            GlobalLogger.log(getClass().getName(), "Couldn't stop empty socket!");
            return;
        }

        isStopped = true;
        socket.close();
        wait();
    }

    /**
     * Listens connections
     */
    public void handle() {
        while (!isStopped) {
            try {
                Socket connection = socket.accept();
                HandleTask task = new HandleTask(connection);
                new Thread(task).start();
            } catch (IOException e) {

            }

            synchronized (this) {
                notify();
            }
        }
    }
}
