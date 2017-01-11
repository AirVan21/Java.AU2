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

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;

@Log
public class TorrentGUI {
    private final Stage stage;
    private final ClientScene clientScene;

    public TorrentGUI(Stage stage) throws IOException {
        this.stage = stage;
        this.clientScene = new ClientScene(this);
        this.stage.setScene(clientScene);
        addExitLogic();
    }

    public void show() {
        stage.show();
    }

    public Optional<String> getUploadFilePath() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose file for uploading");
        File file = fileChooser.showOpenDialog(stage);
        if (file == null) {
            return Optional.empty();
        }

        return file.exists() ? Optional.of(file.getAbsolutePath()) : Optional.empty();
    }

    private void addExitLogic() {
        stage.setOnCloseRequest(event -> {
            try {
                Client client = clientScene.getClient();
                if (client != null) {
                    client.disconnectFromServer();
                }
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
        dialog.setScene(new Scene(pane, 300, 250));

        return dialog;
    }

    public Stage createDownloadDialog(VBox box) {
        Stage dialog = new Stage();

        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(stage);
        dialog.setTitle("Download");
        dialog.setScene(new Scene(box, 300, 200));

        return dialog;
    }
}
