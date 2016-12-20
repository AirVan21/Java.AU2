package ru.spbau.javacourse.torrent.client;


import lombok.extern.java.Log;
import ru.spbau.javacourse.torrent.database.enity.ClientFileRecord;
import ru.spbau.javacourse.torrent.protocol.ClientServerProtocol;
import ru.spbau.javacourse.torrent.utils.GlobalConstants;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;

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
    private final FileBrowser browser = new FileBrowser(GlobalConstants.DOWNLOAD_DIR);

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
            return;
        }

        timer.cancel();
        socket.close();
    }

    public synchronized void doUpdate() {
        final List<ClientFileRecord> records = browser.getPublishedFileRecords();
        try {
            ClientServerProtocol.sendUpdateToServer(output, port, records);
        } catch (IOException e) {
            log.log(Level.WARNING, e.getMessage());
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
