package ru.spbau.javacourse.torrent.protocol;

import ru.spbau.javacourse.torrent.commands.TorrentRequest;
import ru.spbau.javacourse.torrent.database.enity.ClientFileRecord;
import ru.spbau.javacourse.torrent.database.enity.ServerFileRecord;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 */
public class ClientServerProtocol {

    public static void sendUpdateToServer(DataOutputStream output, short port, List<ClientFileRecord> data) throws IOException {
        output.writeByte(TorrentRequest.GET_UPDATE_REQUEST);
        output.writeShort(port);
        output.writeInt(data.size());
        for (ClientFileRecord record : data) {
            output.writeInt(record.getFileServerId());
        }
        output.flush();
    }

    public static Set<Integer> receiveUpdateFromClient(DataInputStream input) throws IOException {
        Set<Integer> fileIds = new HashSet<>();
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
        output.writeByte(TorrentRequest.GET_UPLOAD_REQUEST);
        output.writeUTF(fileName);
        output.writeLong(fileSize);

        output.flush();
    }

    public static ServerFileRecord receiveUploadFromServer(DataInputStream input, String source) throws IOException {
        String name = input.readUTF();
        long size = input.readLong();

        return new ServerFileRecord(name, size, source);
    }

    public static int receiveUplodaResponseFromServer(DataInputStream input) throws IOException {
        return input.readInt();
    }
}
