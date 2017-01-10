package ru.spbau.javacourse.torrent.tracker;


import lombok.extern.java.Log;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.logging.Level;

@Log
public abstract class Server {
    protected ServerSocket socket;
    protected volatile boolean isStopped;

    /**
     * Starts server
     * @param port start port
     * @throws IOException
     */
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
                log.log(Level.WARNING, e.getMessage());
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
    protected abstract void acceptTask() throws IOException;
}
