package ru.spbau.javacourse.ftp.server;

import ru.spbau.javacourse.ftp.utils.GlobalLogger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Optional;

public class Server {
    private Optional<ServerSocket> socket;
    private volatile boolean isStopped;

    public Server() {}

    public synchronized void start(int port) throws IOException {
        if (socket.isPresent()) {
            GlobalLogger.log("Server is already up!");
            return;
        }

        socket = Optional.of(new ServerSocket(port));
        isStopped = false;
        new Thread(this::handle).start();
    }

    public synchronized void stop() throws IOException, InterruptedException {
        if (!socket.isPresent()) {
            GlobalLogger.log("Couldn't stop empty socket!");
            return;
        }

        isStopped = true;
        socket.get().close();
        socket = Optional.empty();
        wait();
    }


    public void handle() {
        while (!isStopped) {
            try {
                Socket connection = socket.get().accept();
                HandleTask task = new HandleTask(connection);
                new Thread(task).start();
            } catch (IOException e) {
                GlobalLogger.log(e.getMessage());
            }

            synchronized (this) {
                notify();
            }
        }
    }
}


