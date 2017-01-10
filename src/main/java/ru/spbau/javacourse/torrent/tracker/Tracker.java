package ru.spbau.javacourse.torrent.tracker;

import ru.spbau.javacourse.torrent.database.ServerDataBase;
import ru.spbau.javacourse.torrent.database.enity.ServerFileRecord;
import ru.spbau.javacourse.torrent.database.enity.User;
import ru.spbau.javacourse.torrent.tasks.HandleTrackerTask;


import java.io.IOException;
import java.net.Socket;
import java.util.Set;

/**
 * Tracker class
 * (Handles client to tracker requests)
 */
public class Tracker extends Server {
    private final ServerDataBase serverDataBase = new ServerDataBase();

    /**
     * Adds user update information to ServerDataBase
     * @param user user which sent update message
     * @param ids file ids
     */
    public synchronized void addUserInformation(User user, Set<Integer> ids) {
        serverDataBase.addUserInformation(user, ids);
    }

    /**
     * Adds new file record to ServerDataBase
     * @param record file record
     */
    public synchronized void addServerFileRecord(ServerFileRecord record) {
        serverDataBase.addFileRecord(record);
    }

    /**
     * Returns active tracker users
     * @return set of active users
     */
    public synchronized Set<User> getUsers() {
        return serverDataBase.getUsers();
    }

    /**
     * Returns active tracker files
     * @return set of available files
     */
    public synchronized Set<ServerFileRecord> getServerFileRecords() {
        return serverDataBase.getRecords();
    }

    /**
     * Returns users who have file
     * @param fileIds - id of file
     * @return set of seeds
     */
    public synchronized Set<User> getSeeds(int fileIds) {
        return serverDataBase.getSeeds(fileIds);
    }

    @Override
    protected void acceptTask() throws IOException {
        final Socket connection;
        try {
            connection = socket.accept();
        } catch (IOException e) {
            return;
        }

        final HandleTrackerTask task = new HandleTrackerTask(this, connection);
        new Thread(task).start();
    }

    @Override
    public synchronized void stop() throws IOException, InterruptedException {
        serverDataBase.close();
        super.stop();
    }
}
