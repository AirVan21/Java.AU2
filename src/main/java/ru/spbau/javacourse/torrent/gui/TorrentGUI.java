package ru.spbau.javacourse.torrent.gui;


import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.extern.java.Log;
import ru.spbau.javacourse.torrent.client.Client;
import ru.spbau.javacourse.torrent.database.enity.SimpleFileRecord;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;

@Log
public class TorrentGUI {
    private final Stage stage;
    private final Client client;
    private final Scene clientScene;

    public TorrentGUI(Stage stage, Client client) throws IOException {
        this.stage = stage;
        this.client = client;
        this.clientScene = new ClientScene(client, this);
        this.stage.setScene(clientScene);
        addExitLogic();
    }

    public void start() throws IOException {
        client.connectToServer();
        stage.show();
    }

    public Optional<String> getUploadFilePath() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose file for uploading");
        File file = fileChooser.showOpenDialog(stage);

        return file.exists() ? Optional.of(file.getAbsolutePath()) : Optional.empty();
    }

    private void addExitLogic() {
        stage.setOnCloseRequest(event -> {
            try {
                client.disconnectFromServer();
                System.exit(0);
            } catch (IOException | InterruptedException e) {
                log.log(Level.WARNING, "Error on exit!");
                log.log(Level.WARNING, e.getMessage());
            }
        });
    }

    public Stage createWarning(String message) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(stage);
        dialog.setTitle("Warning");

        StackPane pane = new StackPane();
        VBox box = new VBox(20);
        box.setAlignment(Pos.CENTER);
        pane.getChildren().add(new Text(message));

        Button closeButton = new Button("Close");
        closeButton.setOnAction(event -> dialog.close());
        pane.getChildren().add(closeButton);
        dialog.setScene(new Scene(pane, 300, 250));

        return dialog;
    }

    public Stage createDownloadDialog(SimpleFileRecord record) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(stage);
        dialog.setTitle("Download");

        VBox box = new VBox(30);
        box.setAlignment(Pos.CENTER);
        box.getChildren().add(new Text("File: " + record.getName()));
        box.getChildren().add(new Text("Size: " + record.getSize() + " bytes"));

        dialog.setScene(new Scene(box, 300, 200));

        return dialog;
    }
}
