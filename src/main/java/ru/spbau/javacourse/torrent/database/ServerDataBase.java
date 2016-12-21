package ru.spbau.javacourse.torrent.database;

import ru.spbau.javacourse.torrent.database.enity.ServerFileRecord;
import ru.spbau.javacourse.torrent.database.enity.User;

import java.util.*;

public class ServerDataBase {
    private final Map<User, Set<Integer>> userToIdsMapping = new HashMap<>();
    private final Set<ServerFileRecord> records = new HashSet<>();

    public ServerDataBase() {}

    public synchronized void addUserInformation(User user, Set<Integer> ids) {
        userToIdsMapping.put(user, ids);
    }

    public synchronized void addFileRecord(ServerFileRecord record) {
        records.add(record);
    }

    public synchronized Set<User> getUsers() {
        return userToIdsMapping.keySet();
    }
}
