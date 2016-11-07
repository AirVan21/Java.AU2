package ru.spbau.javacourse.ftp.server;

import ru.spbau.javacourse.ftp.utils.GlobalLogger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Optional;

/**
 * Server class - class which handles Client requests and resolves connections
 */
public class Server {
    private Optional<ServerSocket> socket = Optional.empty();
    private volatile boolean isStopped;

    public Server() {}

    /**
     * Starts server
     * @param port - port for server socket
     * @throws IOException
     */
    public synchronized void start(int port) throws IOException {
        if (socket.isPresent()) {
            GlobalLogger.log(getClass().getName(), "Server is already up!");
            return;
        }

        socket = Optional.of(new ServerSocket(port));
        isStopped = false;
        new Thread(this::handle).start();
    }

    /**
     * Stops server
     * @throws IOException
     * @throws InterruptedException
     */
    public synchronized void stop() throws IOException, InterruptedException {
        if (!socket.isPresent()) {
            GlobalLogger.log(getClass().getName(), "Couldn't stop empty socket!");
            return;
        }

        isStopped = true;
        socket.get().close();
        socket = Optional.empty();
        wait();
    }

    /**
     * Listens connections
     */
    public void handle() {
        while (!isStopped) {
            try {
                Socket connection = socket.get().accept();
                HandleTask task = new HandleTask(connection);
                new Thread(task).start();
            } catch (IOException e) {
                // already closed exception
            }

            synchronized (this) {
                notify();
            }
        }
    }
}


