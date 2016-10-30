package ru.spbau.javacourse.ftp.client;

import ru.spbau.javacourse.ftp.commands.Request;
import ru.spbau.javacourse.ftp.commands.RequestManager;
import ru.spbau.javacourse.ftp.utils.FolderEntity;
import ru.spbau.javacourse.ftp.utils.GlobalLogger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Optional;

/**
 * Client class - implementation of client logic.
 * Sends requests to server:
 *  1. List (listing files and dirs)
 *  2. Get (downloading selected file)
 */
public class Client {
    private final String hostName;
    private final int port;
    private DataInputStream input;
    private DataOutputStream output;
    private Optional<Socket> socket = Optional.empty();

    public Client(String hostName, int port) {
        this.hostName = hostName;
        this.port = port;
    }

    /**
     * Opens connection
     * @throws IOException
     */
    public synchronized void connect() throws IOException {
        if (socket.isPresent()) {
            GlobalLogger.log("Can't reconnect!");
            return;
        }

        socket = Optional.of(new Socket(hostName, port));
        input = new DataInputStream(socket.get().getInputStream());
        output = new DataOutputStream(socket.get().getOutputStream());
    }

    /**
     * Closes connection
     * @throws IOException
     */
    public synchronized void disconnect() throws IOException {
        if (!socket.isPresent()) {
            GlobalLogger.log("Connection is not found!");
        }

        socket.get().close();
        socket = Optional.empty();
    }

    /**
     * Sending List request to server (waiting for result)
     * @param targetPath path where ls should be performed
     * @return list if items which represents files of dirs
     * @throws IOException
     */
    public synchronized List<FolderEntity> executeListRequest(String targetPath) throws IOException {
        output.writeInt(Request.GET_LIST_REQUEST);
        output.writeUTF(targetPath);
        output.flush();

        return RequestManager.getListResponse(input);
    }

    /**
     * Sending file Get request to server
     * @param pathToFile path to target file
     * @param outputFile file where Get result will be written
     * @throws IOException
     */
    public synchronized void executeGetRequest(String pathToFile, File outputFile) throws IOException {
        output.writeInt(Request.GET_FILE_REQUEST);
        output.writeUTF(pathToFile);
        output.flush();

        RequestManager.getFileResponse(input, outputFile);
    }
}
