package ru.spbau.javacourse.torrent.tasks;

import lombok.extern.java.Log;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;

@Log
public class HandleClientTask extends HandleTask {

    public HandleClientTask(Socket connection) throws IOException {
        super(connection);
    }

    @Override
    protected void executeRequest(byte requestId) throws IOException {
        log.log(Level.INFO, "Executes request = " + Byte.toString(requestId));
    }
}
