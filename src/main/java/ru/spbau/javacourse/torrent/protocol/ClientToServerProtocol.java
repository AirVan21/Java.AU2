package ru.spbau.javacourse.torrent.protocol;

import ru.spbau.javacourse.torrent.commands.TorrentRequest;
import ru.spbau.javacourse.torrent.database.enity.SharedFileRecord;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

/**
 *
 */
public class ClientToServerProtocol {

    public static void sendUpdateToServer(DataOutputStream output, short port, List<SharedFileRecord> data) throws IOException {
        output.writeByte(TorrentRequest.GET_UPDATE_REQUEST);
        output.writeShort(port);
        output.writeInt(data.size());
        for (SharedFileRecord record : data) {
            output.writeInt(record.getFileServerId());
        }
        output.flush();
    }
}
