package ru.spbau.javacourse.ftp.client;

import ru.spbau.javacourse.ftp.commands.Request;
import ru.spbau.javacourse.ftp.utils.GlobalLogger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Optional;

public class Client {
    private final String hostName;
    private final int port;
    private DataInputStream input;
    private DataOutputStream output;
    private Optional<Socket> socket;

    public Client(String hostName, int port) {
        this.hostName = hostName;
        this.port = port;
    }

    public synchronized void connect() throws IOException {
        if (socket.isPresent()) {
            GlobalLogger.log("Can't reconnect!");
            return;
        }

        socket = Optional.of(new Socket(hostName, port));
        input = new DataInputStream(socket.get().getInputStream());
        output = new DataOutputStream(socket.get().getOutputStream());
    }

    public synchronized void disconnect() throws IOException {
        if (!socket.isPresent()) {
            GlobalLogger.log("Connection is not found!");
        }

        socket.get().close();
        socket = Optional.empty();
    }

    public synchronized void executeListRequest(String targetPath) throws IOException {
        output.writeInt(Request.GET_LIST_REQUEST);
        output.writeUTF(targetPath);
        output.flush();
    }

    public synchronized void executeGetRequest(String pathToFile) throws IOException {
        output.writeInt(Request.GET_FILE_REQUEST);
        output.writeUTF(pathToFile);
        output.flush();
    }
}
