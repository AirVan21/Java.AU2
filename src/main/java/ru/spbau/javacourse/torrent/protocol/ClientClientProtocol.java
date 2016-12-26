package ru.spbau.javacourse.torrent.protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class ClientClientProtocol {

    public static void sendStatToClient(DataOutputStream output, int fileId) throws IOException {
        output.writeInt(fileId);
        output.flush();
    }

    public static List<Integer> receiveStatToClient(DataInputStream input) throws IOException {
        int count = input.readInt();
        for (int i = 0; i < count; i++) {

        }
    }
}
