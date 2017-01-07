package ru.spbau.javacourse.torrent.client;


import ru.spbau.javacourse.torrent.tasks.HandleClientTask;
import ru.spbau.javacourse.torrent.tracker.Server;

import java.io.IOException;
import java.net.Socket;

public class LocalServer extends Server {
    private final FileBrowser browser;

    public LocalServer(FileBrowser browser) {
        super();
        this.browser = browser;
    }

    @Override
    protected void acceptTask() throws IOException {
        final Socket connection;
        try {
            connection = socket.accept();
        } catch (IOException e) {
            return;
        }

        final HandleClientTask task = new HandleClientTask(connection, browser);
        new Thread(task).start();
    }
}
