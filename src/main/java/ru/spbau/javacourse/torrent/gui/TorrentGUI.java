package ru.spbau.javacourse.torrent.gui;


import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.spbau.javacourse.torrent.client.Client;

import java.io.IOException;

public class TorrentGUI {
    private final Stage stage;
    private final Client client;
    private final Scene clientScene;

    public TorrentGUI(Stage stage, Client client) throws IOException {
        this.stage = stage;
        this.client = client;
        this.clientScene = new ClientScene(client);
        this.stage.setScene(clientScene);
        addExitLogic();
    }

    public void start() throws IOException {
        client.connectToServer();
        stage.show();
    }

    private void addExitLogic() {
        stage.setOnCloseRequest(event -> {
            try {
                client.disconnectFromServer();
                System.exit(0);
            } catch (IOException | InterruptedException ignored) {
            }
        });
    }

}
