package ru.spbau.javacourse.torrent.client;


import lombok.extern.java.Log;
import ru.spbau.javacourse.torrent.database.enity.ClientFileRecord;
import ru.spbau.javacourse.torrent.database.enity.SimpleFileRecord;
import ru.spbau.javacourse.torrent.database.enity.User;
import ru.spbau.javacourse.torrent.protocol.ClientServerProtocol;
import ru.spbau.javacourse.torrent.utils.GlobalConstants;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.*;
import java.util.logging.Level;

/**
 * Client class - implementation of client logic.
 */
@Log
public class Client {
    private final String hostName;
    private final short port;
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;
    private final Timer timer = new Timer();
    private final FileBrowser browser = new FileBrowser(GlobalConstants.DOWNLOAD_DIR);

    public Client(String hostName, short port) {
        this.hostName = hostName;
        this.port = port;
    }

    /**
     * Opens connection
     * @throws IOException
     */
    public synchronized void connectToServer() throws IOException {
        log.log(Level.INFO, "Connects to server");

        if (socket != null) {
            return;
        }
        socket = new Socket(hostName, GlobalConstants.TRACKER_PORT);
        input = new DataInputStream(socket.getInputStream());
        output = new DataOutputStream(socket.getOutputStream());
        subscribe();
    }

    /**
     * Closes connection
     * @throws IOException
     */
    public synchronized void disconnectFromServer() throws IOException {
        log.log(Level.INFO, "Disconnects from server");

        if (socket == null) {
            return;
        }
        timer.cancel();
        socket.close();
    }

    public synchronized Optional<List<SimpleFileRecord>> doList() {
        log.log(Level.INFO, "List command");

        try {
            ClientServerProtocol.sendListToServer(output);
            return Optional.of(ClientServerProtocol.receiveListResponseFromServer(input));
        } catch (IOException e) {
            log.log(Level.WARNING, "Failed List request!");
            log.log(Level.WARNING, e.getMessage());
        }

        return Optional.empty();
    }

    public synchronized Optional<List<User>> doSources(int fileId) {
        log.log(Level.INFO, "Sources command");

        try {
            ClientServerProtocol.sendSourcesToServer(output, fileId);
            return Optional.of(ClientServerProtocol.receiveSourcesResponseFromServer(input));
        } catch (IOException e) {
            log.log(Level.WARNING, "Failed Sources request!");
            log.log(Level.WARNING, e.getMessage());
        }

        return Optional.empty();
    }

    /**
     * Executes update request to server
     */
    public synchronized void doUpdate() {
        log.log(Level.INFO, "Update command!");

        final List<ClientFileRecord> records = browser.getPublishedFileRecords();
        try {
            ClientServerProtocol.sendUpdateToServer(output, port, records);
            ClientServerProtocol.receiveUpdateResponseFromServer(input);
        } catch (IOException e) {
            log.log(Level.WARNING, "Couldn't sent update to server");
            log.log(Level.WARNING, e.getMessage());
        }
    }

    /**
     * Executes upload request to server
     * @param pathToFile
     */
    public synchronized void doUpload(String pathToFile) {
        log.log(Level.INFO, "Upload command!");

        final File file = new File(pathToFile);
        if (!file.exists() || file.isDirectory()) {
            log.log(Level.WARNING, "File path if invalid: " + pathToFile);
            return;
        }

        browser.addLocalFile(file.getAbsolutePath(), file.length());
        try {
            ClientServerProtocol.sendUploadToServer(output, file.getAbsolutePath(), file.length());
            int fileId = ClientServerProtocol.receiveUploadResponseFromServer(input);
            browser.publishLocalFile(file.getAbsolutePath(), fileId);
        } catch (IOException e) {
            log.log(Level.WARNING, "Couldn't sent upload to server");
            log.log(Level.WARNING, e.getMessage());
        }
    }

    /**
     * Schedules Update requests to server
     */
    private synchronized void subscribe() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                doUpdate();
            }
        } // task
                , 60 * 1000       // delay (in milliseconds)
                , 5 * 60 * 1000); // period (in milliseconds)
    }
}
