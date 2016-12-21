package ru.spbau.javacourse.torrent.database;

import ru.spbau.javacourse.torrent.database.enity.ServerFileRecord;
import ru.spbau.javacourse.torrent.database.enity.User;

import java.util.*;
import java.util.stream.Collectors;

public class ServerDataBase {
    private final Map<User, Set<Integer>> userToIdsMapping = new HashMap<>();
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

    }

    private synchronized void initialize() {

    }
}
