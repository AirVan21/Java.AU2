package ru.spbau.javacourse.torrent.gui;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.extern.java.Log;
import ru.spbau.javacourse.torrent.client.Client;
import ru.spbau.javacourse.torrent.database.enity.ClientFileRecord;
import ru.spbau.javacourse.torrent.database.enity.SimpleFileRecord;
import ru.spbau.javacourse.torrent.utils.GlobalConstants;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

@Log
public class ClientGUI {
    private final Stage stage;
    private final ClientScene clientScene = new ClientScene();

    public ClientGUI(Stage primaryStage) throws IOException {
        stage = primaryStage;
        stage.setScene(clientScene);
        addExitLogic();
    }

    public void show() {
        stage.show();
    }

    private void addExitLogic() {
        stage.setOnCloseRequest(event -> {
            try {
                if (clientScene.client != null) {
                    clientScene.client.disconnectFromServer();
                }
                System.exit(0);
            } catch (IOException | InterruptedException e) {
                log.log(Level.WARNING, "Error on exit!");
                log.log(Level.WARNING, e.getMessage());
            }
        });
    }

    private class ClientScene extends Scene {
        private Client client;
        private final Button listButton;
        private final Button loadButton;
        private final Button connectButton;
        private final TextField portField;
        private final ListView<ClientFileRecord> localView;
        private final ListView<SimpleFileRecord> trackerView;
        private final ObservableList<ClientFileRecord> localFiles = FXCollections.observableArrayList();
        private final ObservableList<SimpleFileRecord> trackerFiles = FXCollections.observableArrayList();

        private ClientScene() {
            super(new GridPane(), 700, 400);

            GridPane grid = (GridPane) getRoot();
            grid.setAlignment(Pos.CENTER);
            grid.setHgap(15);
            grid.setVgap(15);
            grid.setPadding(new Insets(25, 25, 25, 25));

            portField = new TextField();
            portField.setText("8840");
            grid.add(portField, 0, 0);

            connectButton = createConnectButton(portField);
            grid.add(connectButton, 1, 0);

            localView = new ListView<>();
            localView.setPrefSize(300, 250);
            localView.setPlaceholder(new Text("Local files"));
            localView.setItems(localFiles);
            grid.add(localView, 0, 2);

            trackerView = new ListView<>();
            trackerView.setPrefSize(300, 200);
            trackerView.setPlaceholder(new Text("Tracker files"));
            trackerView.setItems(trackerFiles);
            grid.add(trackerView, 1, 2);
            trackerView.setOnMouseClicked(event -> {
                final SimpleFileRecord record = trackerView.getSelectionModel().getSelectedItem();
                if (record != null) {
                    startDownloadDialog(record);
                }
            });

            listButton = new Button("List files");
            listButton.setOnAction(event -> updateTrackerFiles());
            grid.add(listButton, 1, 4);

            loadButton = createUploadButton();
            grid.add(loadButton, 0, 4);

            disableDownloadLogic();
        }

        private void createClient(short port) {
            client = new Client(GlobalConstants.DEFAULT_HOST, port);
            try {
                client.connectToServer();
            } catch (IOException e) {
                SharedGUI.createWarningDialog(stage, "Failed to connect to Server!");
                return;
            }
            enableDownloadLogic();
        }

        private void updateTrackerFiles() {
            Optional<List<SimpleFileRecord>> records = client.doList();
            if (records.isPresent()) {
                trackerFiles.clear();
                records.get().forEach(trackerFiles::add);
            }
        }

        private void updateLocalFiles() {
            List<ClientFileRecord> records = client.getFileRecords("isPublished", true);
            localFiles.clear();
            records.forEach(localFiles::add);
        }

        private Button createUploadButton() {
            Button loadButton = new Button("Upload file");
            loadButton.setOnAction(event -> {
                Optional<String> pathToFile = getUploadFilePath();
                if (pathToFile.isPresent()) {
                    try {
                        client.doUpload(pathToFile.get());
                        updateTrackerFiles();
                        updateLocalFiles();
                    } catch (Exception e) {
                        SharedGUI.createWarningDialog(stage, "Failed on uploading: " + e.getMessage());
                    }
                }
            });

            return loadButton;
        }

        private Button createConnectButton(TextField portField) {
            Button connectButton = new Button("Connect to tracker");
            connectButton.setOnMouseClicked(event -> {
                if (portField.getText().isEmpty()) {
                    SharedGUI.createWarningDialog(stage, "Please, enter client port!");
                    return;
                }
                short port = 8080;
                try {
                    port = Short.parseShort(portField.getText());
                } catch (NumberFormatException e) {
                    SharedGUI.createWarningDialog(stage, "Port should be a 4 digit number!");
                    return;
                }
                createClient(port);
                updateLocalFiles();

            });

            return connectButton;
        }

        private void startDownloadDialog(SimpleFileRecord record) {
            VBox box = new VBox(30);
            box.setAlignment(Pos.CENTER);
            box.getChildren().add(new Text("File: " + record.getName()));
            box.getChildren().add(new Text("Size: " + record.getSize() + " bytes"));

            Stage dialog = createDownloadDialog(box);
            Button downloadButton = new Button("Download");
            box.getChildren().add(downloadButton);
            dialog.show();

            downloadButton.setOnAction(event -> {
                List<ClientFileRecord> localRecords = client.getFileRecords("fileServerId", record.getId());
                if (!localRecords.isEmpty()) {
                    box.getChildren().add(new Text("File is already downloaded!"));
                    return;
                }

                if (client.doGet(record.getId())) {
                    box.getChildren().add(new Text("Downloaded file!"));
                } else {
                    box.getChildren().add(new Text("Failed!"));
                }
                updateLocalFiles();
            });
        }

        private Optional<String> getUploadFilePath() {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choose file for uploading");
            File file = fileChooser.showOpenDialog(stage);
            if (file == null) {
                return Optional.empty();
            }

            return file.exists() ? Optional.of(file.getAbsolutePath()) : Optional.empty();
        }

        private Stage createDownloadDialog(VBox box) {
            Stage dialog = new Stage();

            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(stage);
            dialog.setTitle("Download");
            dialog.setScene(new Scene(box, 300, 200));

            return dialog;
        }

        private void enableDownloadLogic() {
            listButton.setDisable(false);
            loadButton.setDisable(false);
            localView.setDisable(false);
            trackerView.setDisable(false);

            // Connection logic is disabled
            portField.setDisable(true);
            connectButton.setDisable(true);

        }

        private void disableDownloadLogic() {
            listButton.setDisable(true);
            loadButton.setDisable(true);
            localView.setDisable(true);
            trackerView.setDisable(true);

            // Connection logic is enabled
            portField.setDisable(false);
            connectButton.setDisable(false);
        }
    }
}
