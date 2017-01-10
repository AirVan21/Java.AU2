package ru.spbau.javacourse.torrent.gui;


import javafx.stage.Stage;
import ru.spbau.javacourse.torrent.client.Client;

public class TorrentGUI {
    private final Stage stage;
    private final Client client;

    public TorrentGUI(Stage stage, Client client) {
        this.stage = stage;
        this.client = client;
    }

    public void show() {
        stage.show();
    }
}
