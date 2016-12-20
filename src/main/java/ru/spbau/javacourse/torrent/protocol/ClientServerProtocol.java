package ru.spbau.javacourse.torrent.protocol;

import ru.spbau.javacourse.torrent.commands.TorrentRequest;
import ru.spbau.javacourse.torrent.database.enity.ClientFileRecord;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

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

    public static void sendUpdateFromServer(DataInputStream input) throws IOException {
        short port = input.readShort();
        final int size = input.readInt();
        while (size > 0) {
            int fileId = input.readInt();
        }
    }

    public static void sendUploadToServer(DataOutputStream output, String fileName, long fileSize) throws IOException {
        output.writeByte(TorrentRequest.GET_UPLOAD_REQUEST);
        output.writeUTF(fileName);
        output.writeLong(fileSize);
        output.flush();
    }
}
