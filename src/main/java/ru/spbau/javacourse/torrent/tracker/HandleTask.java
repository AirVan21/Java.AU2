package ru.spbau.javacourse.torrent.tracker;

import ru.spbau.javacourse.torrent.commands.TorrentRequest;
import ru.spbau.javacourse.torrent.utils.GlobalLogger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * HandleTask is a class which handles Client request (and executes it)
 */
public class HandleTask implements Runnable {
    private final Socket taskSocket;
    private final DataInputStream input;
    private final DataOutputStream output;

    public HandleTask(Socket connection) throws IOException {
        taskSocket = connection;
        input = new DataInputStream(taskSocket.getInputStream());
        output = new DataOutputStream(taskSocket.getOutputStream());
    }

    /**
     * Parses and executes client request
     */
    @Override
    public void run() {
        try {
            while (!taskSocket.isClosed()) {
                final int requestId = taskSocket.isClosed() ? 0 : input.readInt();
                executeRequest(requestId);
            }
        }
        catch (IOException e) {
            GlobalLogger.log(HandleTask.class.getName(), e.getMessage());
        } finally {
            try {
                taskSocket.close();
            } catch (IOException e) {
                GlobalLogger.log(getClass().getName(), e.getMessage());
            }
        }
    }

    /**
     * Chooses between LIST and GET requests and executes
     * @param requestId id of request
     * @throws IOException
     */
    private void executeRequest(int requestId) throws IOException {
        switch (requestId) {
            case TorrentRequest.GET_LIST_REQUEST:
                break;
            case TorrentRequest.GET_SOURCES_REQUEST:
                break;
            case TorrentRequest.GET_UPDATE_REQUEST:
                break;
            case TorrentRequest.GET_UPLOAD_REQUEST:
                break;
            default:
                taskSocket.close();
        }
    }
}
