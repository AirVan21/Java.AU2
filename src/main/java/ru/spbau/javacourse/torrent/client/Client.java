package ru.spbau.javacourse.torrent.client;


import ru.spbau.javacourse.torrent.database.enity.SharedFileRecord;
import ru.spbau.javacourse.torrent.utils.GlobalLogger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Client class - implementation of client logic.
 */
public class Client {
    private final String hostName;
    private final short port;
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;
    private final Timer timer = new Timer();
    private final FileBrowser browser = new FileBrowser();

    public Client(String hostName, short port) {
        this.hostName = hostName;
        this.port = port;
    }

    /**
     * Opens connection
     * @throws IOException
     */
    public synchronized void connectToServer() throws IOException {
        if (socket != null) {
            GlobalLogger.log(getClass().getName(), "Can't reconnect!");
            return;
        }

        socket = new Socket(hostName, port);
        input = new DataInputStream(socket.getInputStream());
        output = new DataOutputStream(socket.getOutputStream());
        subscribe();
    }

    /**
     * Closes connection
     * @throws IOException
     */
    public synchronized void disconnectFromServer() throws IOException {
        if (socket == null) {
            GlobalLogger.log(getClass().getName(), "Connection is not found!");
            return;
        }

        timer.cancel();
        input.close();
        output.close();
        socket.close();
    }

    public void doUpdate() {
        final List<SharedFileRecord> records = browser.
    }

    private synchronized void subscribe() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                doUpdate();
            }
        } // task
                , 0 // delay (in milliseconds)
                , 5 * 60* 1000); // period (in milliseconds)
    }
}
