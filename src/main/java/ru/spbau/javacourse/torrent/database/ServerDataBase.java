package ru.spbau.javacourse.torrent.database;

import lombok.extern.java.Log;
import ru.spbau.javacourse.torrent.database.enity.ServerFileRecord;
import ru.spbau.javacourse.torrent.database.enity.User;


import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Log
public class ServerDataBase {
    public static final String USER_TO_ID = "user-ids.ser";
    public static final String FILE_RECORDS = "records.ser";
    private final ConcurrentHashMap<User, Set<Integer>> userToIdsMapping = new ConcurrentHashMap<>();
    private final Set<ServerFileRecord> records = new HashSet<>();
    private final String serverDBDir;

    public ServerDataBase(String serverDir) {
        serverDBDir = serverDir;
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
        try {
            FileOutputStream fos = new FileOutputStream(USER_TO_ID);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(userToIdsMapping);
            oos.close();
            fos.close();
        } catch (java.io.IOException e) {
            log.log(Level.WARNING, e.getMessage());
        }
    }

    private synchronized void initialize() {

    }

    public synchronized void dropDatabase() {
        userToIdsMapping.clear();
        records.clear();
    }
}
