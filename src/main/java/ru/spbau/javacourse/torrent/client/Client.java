package ru.spbau.javacourse.torrent.client;


import lombok.extern.java.Log;
import ru.spbau.javacourse.torrent.database.enity.SharedFileRecord;
import ru.spbau.javacourse.torrent.protocol.ClientToServerProtocol;

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
@Log
public class Client {
    private final String hostName;
    private final short port;
    private final String userName;
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;
    private final Timer timer = new Timer();
    private final FileBrowser browser = new FileBrowser();

    public Client(String hostName, short port, String userName) {
        this.hostName = hostName;
        this.port = port;
        this.userName = userName;
    }

    /**
     * Opens connection
     * @throws IOException
     */
    public synchronized void connectToServer() throws IOException {
        if (socket != null) {
            log.info("Can't reconnect!");
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
            log.info("Connection is not found!");
            return;
        }

        timer.cancel();
        socket.close();
    }

    public synchronized void doUpdate() {
        final List<SharedFileRecord> records = browser.getPublishedSharedFileRecords();
        try {
            ClientToServerProtocol.sendUpdateToServer(output, port, records);
        } catch (IOException e) {
            log.info("Impossible to send update to server");
        }
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
