package ru.spbau.javacourse.torrent.protocol;

import ru.spbau.javacourse.torrent.commands.ClientRequest;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClientClientProtocol {

    public static void sendStatToClient(DataOutputStream output, int fileId) throws IOException {
        output.writeByte(ClientRequest.GET_STAT_REQUEST);
        output.writeInt(fileId);
        output.flush();
    }

    public static List<Integer> receiveStatToClient(DataInputStream input) throws IOException {
        int count = input.readInt();
        final List<Integer> result = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            result.add(input.readInt());
        }

        return result;
    }

    public static void sendGetToClient(DataOutputStream output, int fileId, int chunkId) throws IOException {
        output.writeByte(ClientRequest.GET_FILE_REQUEST);
        output.writeInt(fileId);
        output.writeInt(chunkId);
        output.flush();
    }

    public static void receiveGetToClient(DataInputStream input) {

    }
}
