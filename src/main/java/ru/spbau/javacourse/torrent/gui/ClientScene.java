package ru.spbau.javacourse.torrent.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import ru.spbau.javacourse.torrent.client.Client;
import ru.spbau.javacourse.torrent.database.enity.SimpleFileRecord;

public class ClientScene extends Scene {
    private final Client client;
    private final ObservableList<String> localFiles = FXCollections.observableArrayList();
    private final ObservableList<SimpleFileRecord> trackerFiles = FXCollections.observableArrayList();

    public ClientScene(Client client) {
        super(new GridPane(), 600, 500);
        this.client = client;

        GridPane grid = (GridPane) getRoot();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(25, 25, 25, 25));

        ListView<String> localView = new ListView<>();
        localView.setPrefSize(300, 250);
        localView.setPlaceholder(new Text("Local files"));
        localView.setItems(localFiles);
        grid.add(localView, 0, 2);
        updateLocalFiles();

        ListView<SimpleFileRecord> trackerView = new ListView<>();
        trackerView.setPrefSize(300, 250);
        trackerView.setPlaceholder(new Text("Tracker files"));
        trackerView.setItems(trackerFiles);
        grid.add(trackerView, 1, 2);

        Button listButton = new Button("List files");
        grid.add(listButton, 0, 0);
        listButton.setOnAction(event -> doList());
    }

    private void doList() {

    }

    private void updateLocalFiles() {
        
    }
}
