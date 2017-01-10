package ru.spbau.javacourse.torrent.database;

import lombok.extern.java.Log;
import ru.spbau.javacourse.torrent.database.enity.ServerFileRecord;
import ru.spbau.javacourse.torrent.database.enity.User;
import ru.spbau.javacourse.torrent.utils.GlobalConstants;


import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Log
public class ServerDataBase {
    private static final String USER_TO_ID = "user-ids.ser";
    private static final String FILE_RECORDS = "records.ser";
    private ConcurrentHashMap<User, Set<Integer>> userToIdsMapping = new ConcurrentHashMap<>();
    private Set<ServerFileRecord> records = new HashSet<>();

    public ServerDataBase() {
        initialize();
    }

    public synchronized void addUserInformation(User user, Set<Integer> ids) {
        userToIdsMapping.put(user, ids);
    }

    public synchronized void addFileRecord(ServerFileRecord record) {
        records.add(record);
    }

    public synchronized Set<User> getUsers() {
        return userToIdsMapping.keySet();
    }

    public synchronized Set<ServerFileRecord> getRecords() {
        return records;
    }

    public synchronized Set<User> getSeeds(int fileId) {
        return userToIdsMapping
                .entrySet()
                .stream()
                .filter(item -> item.getValue().contains(fileId))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    public synchronized void close() {
        FileOutputStream fosMap = null;
        ObjectOutputStream oosMap = null;
        FileOutputStream fosSet = null;
        ObjectOutputStream oosSet = null;
        try {
            fosMap = new FileOutputStream(GlobalConstants.SERVER_DB_DIR + USER_TO_ID);
            oosMap = new ObjectOutputStream(fosMap);
            oosMap.writeObject(userToIdsMapping);

            fosSet = new FileOutputStream(GlobalConstants.SERVER_DB_DIR + FILE_RECORDS);
            oosSet = new ObjectOutputStream(fosSet);
            oosSet.writeObject(records);
        } catch (IOException e) {
            log.log(Level.WARNING, e.getMessage());
        } finally {
            try {
                if (oosMap != null) {
                    oosMap.close();
                }
                if (fosMap != null) {
                    fosMap.close();
                }
                if (oosSet != null) {
                    oosSet.close();
                }
                if (fosSet != null) {
                    fosSet.close();
                }
            } catch (IOException e) {
                log.log(Level.WARNING, "Failed to store server!");
                log.log(Level.WARNING, e.getMessage());
            }
        }
    }

    private synchronized void initialize() {
        File map = new File(GlobalConstants.SERVER_DB_DIR + USER_TO_ID);
        File set = new File(GlobalConstants.SERVER_DB_DIR + FILE_RECORDS);
        FileInputStream fisMap = null;
        ObjectInputStream oisMap = null;
        FileInputStream fisSet = null;
        ObjectInputStream oisSet = null;
        if (!map.exists() || !set.exists()) {
            return;
        }
        try {
            fisMap = new FileInputStream(GlobalConstants.SERVER_DB_DIR + USER_TO_ID);
            oisMap = new ObjectInputStream(fisMap);
            userToIdsMapping = (ConcurrentHashMap) oisMap.readObject();

            fisSet = new FileInputStream(GlobalConstants.SERVER_DB_DIR + FILE_RECORDS);
            oisSet = new ObjectInputStream(fisSet);
            records = (Set) oisSet.readObject();
        } catch (IOException | ClassNotFoundException e) {
            log.log(Level.WARNING, e.getMessage());
        } finally {
            try {
                if (fisMap != null) {
                    fisMap.close();
                }
                if (oisMap != null) {
                    oisMap.close();
                }
                if (oisSet != null) {
                    oisSet.close();
                }
                if (fisSet != null) {
                    fisSet.close();
                }
            } catch (IOException e) {
                log.log(Level.WARNING, "Failed to restore server!");
                log.log(Level.WARNING, e.getMessage());
            }
        }

    }

    public synchronized void dropDatabase() {
        userToIdsMapping.clear();
        records.clear();
    }
}
