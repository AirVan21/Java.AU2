package ru.spbau.javacourse.torrent.client;

import lombok.extern.java.Log;
import ru.spbau.javacourse.torrent.database.enity.User;
import ru.spbau.javacourse.torrent.protocol.ClientClientProtocol;
import ru.spbau.javacourse.torrent.utils.GlobalConstants;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.*;
import java.util.logging.Level;

/**
 * DownloadManager class
 */
@Log
public class DownloadManager {
    private String DOWNLOAD_DIR = GlobalConstants.DOWNLOAD_DIR;

    public static synchronized List<Integer> doHostStat(int fileId, short port) {
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

    public static synchronized void doPartGet(int fileId, int part, short port) {
        log.log(Level.INFO, "PartGet command!");
    }

    public static Map<User, List<Integer>> createSchedule(Map<User, List<Integer>> stat) {
        final Map<User, List<Integer>> schedule = new HashMap<>();
        final Set<Integer> scheduledChunks = new HashSet<>();
        boolean wasTaken;
        do {
            // checks that is possible to add something
            wasTaken = false;
            for (Map.Entry<User, List<Integer>> seed : stat.entrySet()) {
                Optional<Integer> chunk = seed.getValue().stream()
                        .filter(item -> !scheduledChunks.contains(item))
                        .findFirst();
                //
                if (chunk.isPresent()) {
                    wasTaken = true;
                    scheduledChunks.add(chunk.get());
                    final List<Integer> target = schedule.get(seed.getKey());
                    if (target == null) {
                        List<Integer> list = new ArrayList<>();
                        list.add(chunk.get());
                        schedule.put(seed.getKey(), list);
                    } else {
                        target.add(chunk.get());
                    }
                }
            }
        } while (wasTaken);

        return schedule;
    }

    private static synchronized void writeFileChunk(String pathToFile, int chunkId, DataInputStream input) {

    }

    private static synchronized void readFileChunk(String pathToFile, int chunkId, DataOutputStream output) {

    }
}
