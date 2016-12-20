package ru.spbau.javacourse.torrent.tracker;

import ru.spbau.javacourse.torrent.database.ServerDataBase;
import ru.spbau.javacourse.torrent.database.enity.ServerFileRecord;
import ru.spbau.javacourse.torrent.database.enity.User;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Set;

/**
 * Tracker class
 */
public class Tracker {
    private ServerSocket socket;
    private volatile boolean isStopped;
    private final ServerDataBase serverDataBase = new ServerDataBase();

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
     * Adds user update information to ServerDataBase
     * @param user user which sent update message
     * @param ids file ids
     */
    public synchronized void addUserInformation(User user, Set<Integer> ids) {
        serverDataBase.addUserInformation(user, ids);
    }

    /**
     * Adds new file record to ServerDataBase
     * @param record file record
     */
    public synchronized void addServerFileRecord(ServerFileRecord record) {
        serverDataBase.addFileRecord(record);
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

        final HandleTask task = new HandleTask(this, connection);
        new Thread(task).start();
    }
}
