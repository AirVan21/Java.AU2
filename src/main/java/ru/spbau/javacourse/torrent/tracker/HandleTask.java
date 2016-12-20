package ru.spbau.javacourse.torrent.tracker;

import lombok.extern.java.Log;
import ru.spbau.javacourse.torrent.commands.TorrentRequest;
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
     * Chooses between LIST and GET requests and executes
     * @param requestId id of request
     * @throws IOException
     */
    private void executeRequest(byte requestId) throws IOException {
        final Optional<String> host = getHostFromAddress(taskSocket.getRemoteSocketAddress().toString());
        if (!host.isPresent()) {
            return;
        }
        switch (requestId) {
            case TorrentRequest.GET_LIST_REQUEST:
                break;
            case TorrentRequest.GET_SOURCES_REQUEST:
                break;
            case TorrentRequest.GET_UPDATE_REQUEST:
                int port = input.readInt();
                Set<Integer> fileIds = ClientServerProtocol.receiveUpdateFromClient(input);
                tracker.addUserInformation(new User(host.get(), port), fileIds);
                output.writeBoolean(true);
                break;
            case TorrentRequest.GET_UPLOAD_REQUEST:
                ServerFileRecord record = ClientServerProtocol.receiveUploadFromServer(input, host.get());
                tracker.addServerFileRecord(record);
                output.writeInt(record.hashCode());
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
