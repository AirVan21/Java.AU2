package ru.spbau.javacourse.torrent.client;

import lombok.extern.java.Log;
import ru.spbau.javacourse.torrent.protocol.ClientClientProtocol;
import ru.spbau.javacourse.torrent.utils.GlobalConstants;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * DownloadManager class
 */
@Log
public class DownloadManager {

    private static synchronized void doPartGet(int fileId, int part) {
        log.log(Level.INFO, "PartGet command!");
    }

    public static List<Integer> doHostStat(int fileId, int port) {
        log.log(Level.INFO, "HostStat command!");

        List<Integer> result = new ArrayList<>();
        try {
            Socket clientSocket = new Socket(GlobalConstants.DEFAULT_HOST, port);
            final DataOutputStream clientOutput = new DataOutputStream(clientSocket.getOutputStream());
            final DataInputStream clientInput = new DataInputStream(clientSocket.getInputStream());
            ClientClientProtocol.sendStatToClient(clientOutput, fileId);
            result = ClientClientProtocol.receiveStatToClient(clientInput);
        } catch (IOException e) {
            log.log(Level.WARNING, "Failed HostStat!");
            log.log(Level.WARNING, e.getMessage());
        }

        return result;
    }

    public static synchronized void writeFileChunk(String pathToFile, int chunkId, DataInputStream input) {

    }

    public static synchronized void readFileChunk(String pathToFile, int chunkId, DataOutputStream output) {

    }

    public static List<Integer> getFileChunks(String pathToFile) {
        final List<Integer> chunks = new ArrayList<>();
        final File file = new File(pathToFile);
        if (!file.exists() || file.isDirectory()) {
            // empty
            return chunks;
        }

        long amountOfChunks = (file.length() % GlobalConstants.CHUNK_SIZE == 0)
                            ? file.length() / GlobalConstants.CHUNK_SIZE
                            : file.length() / GlobalConstants.CHUNK_SIZE + 1;
        for (int i = 0; i < amountOfChunks; ++i) {
            chunks.add(i);
        }

        return chunks;
    }
}
