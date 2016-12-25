package ru.spbau.javacourse.torrent.tasks;

import lombok.extern.java.Log;
import ru.spbau.javacourse.torrent.commands.TrackerRequest;
import ru.spbau.javacourse.torrent.database.enity.ServerFileRecord;
import ru.spbau.javacourse.torrent.database.enity.User;
import ru.spbau.javacourse.torrent.protocol.ClientServerProtocol;
import ru.spbau.javacourse.torrent.tracker.Tracker;


import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;

/**
 * HandleTrackerTask is a class which handles Client request (and executes it)
 */
@Log
public class HandleTrackerTask extends HandleTask {
    private final Tracker tracker;

    public HandleTrackerTask(Tracker server, Socket connection) throws IOException {
        super(connection);
        tracker = server;
    }

    @Override
    protected void executeRequest(byte requestId) throws IOException {
        log.log(Level.INFO, "Executes request = " + Byte.toString(requestId));

        final Optional<String> host = getHostFromAddress(taskSocket.getRemoteSocketAddress().toString());
        if (!host.isPresent()) {
            return;
        }

        switch (requestId) {
            case TrackerRequest.GET_LIST_REQUEST:
                final Set<ServerFileRecord> records = tracker.getServerFileRecords();
                output.writeInt(records.size());
                for (ServerFileRecord record : records) {
                    output.writeInt(record.hashCode());
                    output.writeUTF(record.getName());
                    output.writeLong(record.getSize());
                }
                break;
            case TrackerRequest.GET_SOURCES_REQUEST:
                int fileId = input.readInt();
                final Set<User> seeds = tracker.getSeeds(fileId);
                output.writeInt(seeds.size());
                for (User user : seeds) {
                    final List<Byte> bytes = transformIpToBytes(user.getHost());
                    for (Byte chunk : bytes) {
                        output.writeByte(chunk);
                    }
                    output.writeShort(user.getPort());
                }
                break;
            case TrackerRequest.GET_UPDATE_REQUEST:
                short port = input.readShort();
                Set<Integer> fileIds = ClientServerProtocol.receiveUpdateFromClient(input);
                tracker.addUserInformation(new User(host.get(), port), fileIds);
                output.writeBoolean(true);
                break;
            case TrackerRequest.GET_UPLOAD_REQUEST:
                ServerFileRecord record = ClientServerProtocol.receiveUploadFromServer(input, host.get());
                tracker.addServerFileRecord(record);
                output.writeInt(record.hashCode());
                break;
            default:
                taskSocket.close();
        }

        output.flush();
    }

    /**
     * Gets ip address from ip:port string
     * @param address - ip:port
     * @return ip
     */
    private Optional<String> getHostFromAddress(String address) {
        String[] parts = address.split(":");
        if (parts.length > 0) {
            String ip = parts[0].substring(1);
            return Optional.of(ip);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Converts ip to list of bytes
     * @param ip - comma separated string
     * @return list of ip bytes
     */
    private List<Byte> transformIpToBytes(String ip) {
        final List<Byte> bytes = new ArrayList<>();
        final String[] chunks = ip.split("\\.");
        for (String chunk : chunks) {
            bytes.add(Byte.parseByte(chunk));
        }

        return bytes;
    }
}
