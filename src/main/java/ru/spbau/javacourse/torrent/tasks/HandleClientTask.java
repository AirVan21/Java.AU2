package ru.spbau.javacourse.torrent.tasks;

import lombok.extern.java.Log;
import ru.spbau.javacourse.torrent.commands.ClientRequest;

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

        switch (requestId) {
            case ClientRequest.GET_STAT_REQUEST:
                break;
            case ClientRequest.GET_FILE_REQUEST:
                break;
        }
    }
}
