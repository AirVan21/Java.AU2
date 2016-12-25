package ru.spbau.javacourse.torrent.tasks;

import lombok.extern.java.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;

@Log
public abstract class HandleTask implements Runnable {
    protected final Socket taskSocket;
    protected final DataInputStream input;
    protected final DataOutputStream output;

    public HandleTask(Socket connection) throws IOException {
        taskSocket = connection;
        input = new DataInputStream(taskSocket.getInputStream());
        output = new DataOutputStream(taskSocket.getOutputStream());
    }

    /**
     * Chooses between requests and executes
     * @param requestId id of request
     * @throws IOException
     */
    abstract void executeRequest(byte requestId) throws IOException;

    @Override
    public void run() {
        log.log(Level.INFO, "Started HandleTrackerTask");
        try {
            while (!taskSocket.isClosed()) {
                final byte requestId = input.readByte();
                executeRequest(requestId);
            }
        }
        catch (IOException e) {

        } finally {
            try {
                taskSocket.close();
            } catch (IOException e) {
                log.log(Level.WARNING, e.getMessage());
            }
        }
    }
}
