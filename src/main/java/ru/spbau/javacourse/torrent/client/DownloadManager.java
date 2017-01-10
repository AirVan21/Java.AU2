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
    public static final String READ_WRITE = "rw";
    public static final String READ = "r";

    /**
     * Asks chunk information about file
     * @param fileId id of the file
     * @param port id of the chunk
     * @return list of available chunk ids
     */
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

    /**
     * Downloads file chunks (and updates db)
     */
    public static void doHostGet(int fileId, String filePath, long fileSize, List<Integer> chunks, short port, FileBrowser browser) {
        log.log(Level.INFO, "Get for port = " + port);

        for (Integer chunkId : chunks) {
            if (doPartGet(fileId, filePath, fileSize, chunkId, port)) {
                browser.addAvailableChunk(fileId, chunkId);
            }
        }
    }

    /**
     * Downloads selected file chunk
     */
    private static boolean doPartGet(int fileId, String filePath, long fileSize, int chunkId, short port) {
        log.log(Level.INFO, "PartGet command for chunk = " + chunkId);

        boolean isSuccess = true;
        Socket clientSocket = null;
        try {
            clientSocket = new Socket(GlobalConstants.DEFAULT_HOST, port);
            final DataOutputStream clientOutput = new DataOutputStream(clientSocket.getOutputStream());
            final DataInputStream clientInput = new DataInputStream(clientSocket.getInputStream());
            ClientClientProtocol.sendGetToClient(clientOutput, fileId, chunkId);
            long chunkLength = getReadLength(fileSize, chunkId);
            byte[] data = ClientClientProtocol.receiveGetToClient(clientInput, chunkLength);
            DownloadManager.writeFileChunk(filePath, chunkId, data);
        } catch (IOException e) {
            isSuccess = false;
            log.log(Level.WARNING, "Failed HostStat!");
            log.log(Level.WARNING, e.getMessage());
        } finally {
            if (clientSocket != null) {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    log.log(Level.INFO, "Couldn't close socket!");
                }
            }
        }

        return isSuccess;
    }

    /**
     * Creates schedule for sequential downloading
     * @param stat statistics about available chunks
     * @return schedule
     */
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

    /**
     * Writes file chunk to input stream
     * @param pathToFile path to file where we will store data
     * @param chunkId id of chunk
     * @param data data which will be stored
     */
    public static synchronized boolean writeFileChunk(String pathToFile, int chunkId, byte[] data) {
        log.log(Level.INFO, "Write file chunk to " + pathToFile);

        boolean success = true;
        RandomAccessFile accessFile = null;
        long position = chunkId * GlobalConstants.CHUNK_SIZE;
        verifyPath(pathToFile);
        try {
            accessFile = new RandomAccessFile(pathToFile, READ_WRITE);
            accessFile.seek(position);
            accessFile.write(data);
        } catch (IOException e) {
            success = false;
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

        return success;
    }

    /**
     * Reads file chunk to output stream
     * @param pathToFile path to file which will be read
     * @param chunkId id of target chunk
     * @param output place where read data will be stored
     */
    public static synchronized boolean readFileChunk(String pathToFile, int chunkId, DataOutputStream output) {
        log.log(Level.INFO, "Reading file chunk from " + pathToFile);

        boolean isSuccess = true;
        RandomAccessFile accessFile = null;
        int position = chunkId * (int) GlobalConstants.CHUNK_SIZE;
        try {
            accessFile = new RandomAccessFile(pathToFile, READ);
            accessFile.seek(position);
            int readLength = (int) getReadLength(accessFile.length(), chunkId);
            byte[] data = new byte[readLength];
            accessFile.readFully(data);
            output.write(data);
        } catch (IOException e) {
            isSuccess = false;
            log.log(Level.WARNING, "Failed to read file chunk for " + pathToFile);
            log.log(Level.WARNING, e.getMessage());
        } finally {
            try {
                if (accessFile != null) {
                    accessFile.close();
                }
            } catch (IOException e) {
                log.log(Level.WARNING, "Couldn't close Random Access file for " + pathToFile);
            }
        }

        return isSuccess;
    }

    /**
     * Creates folders and target file
     * @param pathToFile path to target file
     */
    private static void verifyPath(String pathToFile) {
        final File file = new File(pathToFile);
        if (file.exists()) {
            return;
        }

        try {
            file.getParentFile().mkdirs();
            file.createNewFile();
        } catch (IOException e) {
            log.log(Level.WARNING, "Failed to create file " + pathToFile);
        }
    }

    private static long getReadLength(long fileSize, int chunkId) {
        long position = chunkId * GlobalConstants.CHUNK_SIZE;
        long diff = fileSize - position;
        return diff >= GlobalConstants.CHUNK_SIZE
                ? GlobalConstants.CHUNK_SIZE
                : diff;
    }
}
