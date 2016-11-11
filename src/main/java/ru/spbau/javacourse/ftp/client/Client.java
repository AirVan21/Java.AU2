package ru.spbau.javacourse.ftp.client;

import lombok.extern.java.Log;
import ru.spbau.javacourse.ftp.commands.Request;
import ru.spbau.javacourse.ftp.commands.RequestManager;
import ru.spbau.javacourse.ftp.utils.FolderEntity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Client class - implementation of client logic.
 * Sends requests to server:
 *  1. List (listing files and dirs)
 *  2. Get (downloading selected file)
 */
@Log
public class Client {
    private final String hostName;
    private final int port;
    private DataInputStream input;
    private DataOutputStream output;
    private Socket socket;

    public Client(String hostName, int port) {
        this.hostName = hostName;
        this.port = port;
    }

    /**
     * Opens connection
     * @throws IOException
     */
    public synchronized void connect() throws IOException {
        if (socket != null) {
            log.info("Can't reconnect!");
            return;
        }

        socket = new Socket(hostName, port);
        input = new DataInputStream(socket.getInputStream());
        output = new DataOutputStream(socket.getOutputStream());
    }

    /**
     * Closes connection
     * @throws IOException
     */
    public synchronized void disconnect() throws IOException {
        if (socket == null) {
            log.info("Connection is not found!");
            return;
        }

        // closes input/output streams
        socket.close();
    }

    /**
     * Sending List request to server (waiting for result)
     * @param targetPath path where ls should be performed
     * @return list if items which represents files of dirs
     */
    public synchronized List<FolderEntity> executeListRequest(String targetPath) {
        List<FolderEntity> result = new ArrayList<>();
        try {
            output.writeInt(Request.GET_LIST_REQUEST);
            output.writeUTF(targetPath);
            output.flush();
            result = RequestManager.getListResponse(input);
        } catch (IOException e) {
            log.info(e.getMessage());
            try {
                disconnect();
            } catch (IOException closeException) {
                log.info("Exception on close!");
            }
        }

        return result;
    }

    /**
     * Sending file Get request to server
     * @param pathToFile path to target file
     * @param outputFile file where Get result will be written
     */
    public synchronized void executeGetRequest(String pathToFile, File outputFile) {
        try {
            output.writeInt(Request.GET_FILE_REQUEST);
            output.writeUTF(pathToFile);
            output.flush();
        } catch (IOException e) {
            log.info(e.getMessage());
            try {
                disconnect();
            } catch (IOException closeException) {
                log.info("Exception on close!");
            }
        }

        RequestManager.getFileResponse(input, outputFile);
    }
}
