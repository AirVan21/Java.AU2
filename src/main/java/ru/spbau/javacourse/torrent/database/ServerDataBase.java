package ru.spbau.javacourse.torrent.database;

import ru.spbau.javacourse.torrent.database.enity.ServerFileRecord;
import ru.spbau.javacourse.torrent.database.enity.User;

import java.util.*;

public class ServerDataBase {
    private Map<User, List<ServerFileRecord>> userToFilesMapping = new HashMap<>();

    public ServerDataBase() {
    }

    public void addServerFileRecords(User user, List<ServerFileRecord> records) {
    }
}
