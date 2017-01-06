package ru.spbau.javacourse.torrent.client;

import lombok.extern.java.Log;
import ru.spbau.javacourse.torrent.database.enity.User;
import ru.spbau.javacourse.torrent.protocol.ClientClientProtocol;
import ru.spbau.javacourse.torrent.utils.GlobalConstants;

import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.logging.Level;

/**
 * DownloadManager class
 */
@Log
public class DownloadManager {
    private static String DOWNLOAD_DIR = GlobalConstants.DOWNLOAD_DIR;

    public static List<Integer> doHostStat(int fileId, short port) {
        log.log(Level.INFO, "HostStat command!");

        List<Integer> result = new ArrayList<>();
        Socket clientSocket = null;
        try {
            clientSocket = new Socket(GlobalConstants.DEFAULT_HOST, port);
            final DataOutputStream clientOutput = new DataOutputStream(clientSocket.getOutputStream());
            final DataInputStream clientInput = new DataInputStream(clientSocket.getInputStream());
            ClientClientProtocol.sendStatToClient(clientOutput, fileId);
            result = ClientClientProtocol.receiveStatToClient(clientInput);
        } catch (IOException e) {
            log.log(Level.WARNING, "Failed HostStat!");
            log.log(Level.WARNING, e.getMessage());
        } finally {
            if (clientSocket != null) {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    log.log(Level.WARNING, e.getMessage());
                }
            }
        }

        return result;
    }

    public static void doHostGet(int fileId, List<Integer> chunks, short port) {
        for (Integer chunkId : chunks) {
            doPartGet(fileId, chunkId, port);
        }
    }

    public static void doPartGet(int fileId, int part, short port) {
        log.log(Level.INFO, "PartGet command!");
        try {
            Socket clientSocket = new Socket(GlobalConstants.DEFAULT_HOST, port);
            final DataOutputStream clientOutput = new DataOutputStream(clientSocket.getOutputStream());
            final DataInputStream clientInput = new DataInputStream(clientSocket.getInputStream());
            ClientClientProtocol.sendGetToClient(clientOutput, fileId, part);
        } catch (IOException e) {
            log.log(Level.WARNING, "Failed HostStat!");
            log.log(Level.WARNING, e.getMessage());
        }
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

    public static synchronized void writeFileChunk(String path, int chunkId, DataInputStream input) {
        String pathToFile = DOWNLOAD_DIR + path;
        RandomAccessFile accessFile = null;
        try {
            final File file = new File(pathToFile);
            if (!file.exists()) {
                file.createNewFile();
            }
            byte[] data = new byte[(int) GlobalConstants.CHUNK_SIZE];
            int size = input.read(data, 0, (int) GlobalConstants.CHUNK_SIZE);
            if (size != GlobalConstants.CHUNK_SIZE) {
                log.log(Level.WARNING, "Hadn't get full chunk!");
            }

            accessFile = new RandomAccessFile(pathToFile, "w");
            accessFile.seek(chunkId * GlobalConstants.CHUNK_SIZE);
            accessFile.write(data);
        } catch (IOException e) {
            log.log(Level.WARNING, "Failed to write chunk!");
            log.log(Level.WARNING, e.getMessage());
        } finally {
            if (accessFile != null) {
                try {
                    accessFile.close();
                } catch (IOException e) {
                    log.log(Level.WARNING, e.getMessage());
                }
            }
        }
    }

    public static synchronized void readFileChunk(String path, int chunkId, DataOutputStream output) {

    }
}
