package ru.spbau.javacourse.torrent.tracker;

import lombok.extern.java.Log;
import ru.spbau.javacourse.torrent.commands.TrackerRequest;
import ru.spbau.javacourse.torrent.database.enity.ServerFileRecord;
import ru.spbau.javacourse.torrent.database.enity.User;
import ru.spbau.javacourse.torrent.protocol.ClientServerProtocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;

/**
 * HandleTask is a class which handles Client request (and executes it)
 */
@Log
public class HandleTask implements Runnable {
    private final Tracker tracker;
    private final Socket taskSocket;
    private final DataInputStream input;
    private final DataOutputStream output;

    public HandleTask(Tracker server, Socket connection) throws IOException {
        tracker = server;
        taskSocket = connection;
        input = new DataInputStream(taskSocket.getInputStream());
        output = new DataOutputStream(taskSocket.getOutputStream());
    }

    /**
     * Parses and executes client request
     */
    @Override
    public void run() {
        log.log(Level.INFO, "Started HandleTask");

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

    /**
     * Chooses between requests and executes
     * @param requestId id of request
     * @throws IOException
     */
    private void executeRequest(byte requestId) throws IOException {
        log.log(Level.INFO, "Executes request = " + Byte.toString(requestId));

        final Optional<String> host = getHostFromAddress(taskSocket.getRemoteSocketAddress().toString());
        if (!host.isPresent()) {
            return;
        }
        switch (requestId) {
            case TrackerRequest.GET_LIST_REQUEST:
                break;
            case TrackerRequest.GET_SOURCES_REQUEST:
                break;
            case TrackerRequest.GET_UPDATE_REQUEST:
                short port = input.readShort();
                Set<Integer> fileIds = ClientServerProtocol.receiveUpdateFromClient(input);
                tracker.addUserInformation(new User(host.get(), port), fileIds);
                output.writeBoolean(true);
                output.flush();
                break;
            case TrackerRequest.GET_UPLOAD_REQUEST:
                ServerFileRecord record = ClientServerProtocol.receiveUploadFromServer(input, host.get());
                tracker.addServerFileRecord(record);
                output.writeInt(record.hashCode());
                output.flush();
                break;
            default:
                taskSocket.close();
        }
    }

    private Optional<String> getHostFromAddress(String address) {
        String[] parts = address.split(":");
        return parts.length > 1 ? Optional.of(parts[0]) : Optional.empty();
    }
}
