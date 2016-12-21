package ru.spbau.javacourse.torrent.protocol;

import ru.spbau.javacourse.torrent.commands.TrackerRequest;
import ru.spbau.javacourse.torrent.database.enity.ClientFileRecord;
import ru.spbau.javacourse.torrent.database.enity.ServerFileRecord;
import ru.spbau.javacourse.torrent.database.enity.SimpleFileRecord;
import ru.spbau.javacourse.torrent.database.enity.User;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

/**
 *
 */
public class ClientServerProtocol {

    public static void sendUpdateToServer(DataOutputStream output, short port, List<ClientFileRecord> data) throws IOException {
        output.writeByte(TrackerRequest.GET_UPDATE_REQUEST);
        output.writeShort(port);
        output.writeInt(data.size());
        for (ClientFileRecord record : data) {
            output.writeInt(record.getFileServerId());
        }

        output.flush();
    }

    public static Set<Integer> receiveUpdateFromClient(DataInputStream input) throws IOException {
        final Set<Integer> fileIds = new HashSet<>();
        int size = input.readInt();
        while (size > 0) {
            fileIds.add(input.readInt());
            size--;
        }

        return fileIds;
    }

    public static boolean receiveUpdateResponseFromServer(DataInputStream inputStream) throws IOException {
        return inputStream.readBoolean();
    }

    public static void sendUploadToServer(DataOutputStream output, String fileName, long fileSize) throws IOException {
        output.writeByte(TrackerRequest.GET_UPLOAD_REQUEST);
        output.writeUTF(fileName);
        output.writeLong(fileSize);

        output.flush();
    }

    public static ServerFileRecord receiveUploadFromServer(DataInputStream input, String source) throws IOException {
        String name = input.readUTF();
        long size = input.readLong();

        return new ServerFileRecord(name, size, source);
    }

    public static int receiveUploadResponseFromServer(DataInputStream input) throws IOException {
        return input.readInt();
    }

    public static void sendListToServer(DataOutputStream output) throws IOException {
        output.writeByte(TrackerRequest.GET_LIST_REQUEST);
        output.flush();
    }

    public static List<SimpleFileRecord> receiveListResponseFromServer(DataInputStream input) throws IOException {
        final List<SimpleFileRecord> result = new ArrayList<>();
        int count = input.readInt();
        for (int i = 0; i < count; i++) {
            int serverId = input.readInt();
            String name = input.readUTF();
            long fileSize = input.readLong();
            result.add(new SimpleFileRecord(serverId, name, fileSize));
        }

        return result;
    }

    public static void sendSourcesToServer(DataOutputStream output, int fileId) throws IOException {
        output.writeByte(TrackerRequest.GET_SOURCES_REQUEST);
        output.writeInt(fileId);

        output.flush();
    }

    public static List<User> receiveSourcesResponseFromServer(DataInputStream input) throws IOException {
        int count = input.readInt();
        final List<User> result = new ArrayList<>();
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(input.readByte());
            for (int j = 0; j < 3; j++) {
                sb.append(".");
                sb.append(input.readByte());
            }
            short port = input.readShort();
            result.add(new User(sb.toString(), port));
        }

        return result;
    }
}
