package ru.spbau.javacourse.torrent.client;

import lombok.extern.java.Log;
import ru.spbau.javacourse.torrent.database.enity.User;
import ru.spbau.javacourse.torrent.protocol.ClientClientProtocol;
import ru.spbau.javacourse.torrent.utils.GlobalConstants;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    public static void doHostGet(int fileId, String filePath, List<Integer> chunks, short port) {
        for (Integer chunkId : chunks) {
            doPartGet(fileId, filePath, chunkId, port);
        }
    }

    public static void doPartGet(int fileId, String filePath, int part, short port) {
        log.log(Level.INFO, "PartGet command!");
        try {
            Socket clientSocket = new Socket(GlobalConstants.DEFAULT_HOST, port);
            final DataOutputStream clientOutput = new DataOutputStream(clientSocket.getOutputStream());
            final DataInputStream clientInput = new DataInputStream(clientSocket.getInputStream());
            ClientClientProtocol.sendGetToClient(clientOutput, fileId, part);
            byte[] data = ClientClientProtocol.receiveGetToClient(clientInput);
            DownloadManager.writeFileChunk(filePath, part, data);
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

    public static synchronized void writeFileChunk(String pathToFile, int chunkId, byte[] data) {
        RandomAccessFile accessFile = null;
        try {
            File file = new File(pathToFile);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }

            accessFile = new RandomAccessFile(pathToFile, "rw");
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

    /**
     * Reads file chunk to output stream
     * @param pathToFile path to file which will be read
     * @param chunkId id of target chunk
     * @param output place where read data will be stored
     */
    public static synchronized void readFileChunk(String pathToFile, int chunkId, DataOutputStream output) {
        int position = chunkId * (int) GlobalConstants.CHUNK_SIZE;
        try {
            RandomAccessFile accessFile = new RandomAccessFile(pathToFile, "r");
            accessFile.seek(position);
            byte[] data = new byte[(int) GlobalConstants.CHUNK_SIZE];
            accessFile.read(data, position, (int) GlobalConstants.CHUNK_SIZE);
            output.write(data);
        } catch (IOException e) {
            log.log(Level.WARNING, "Failed to read file chunk!");
            log.log(Level.WARNING, e.getMessage());
        }
    }
}
