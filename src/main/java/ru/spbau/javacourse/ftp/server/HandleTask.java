package ru.spbau.javacourse.ftp.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class HandleTask implements Runnable {
    private final Socket taskSocket;
    private DataInputStream input;
    private DataOutputStream output;

    public HandleTask(Socket connection) {
        taskSocket = connection;
    }

    public void Initialize() throws IOException {
        input = new DataInputStream(taskSocket.getInputStream());
        output = new DataOutputStream(taskSocket.getOutputStream());
    }

    @Override
    public void run() {

    }
}
