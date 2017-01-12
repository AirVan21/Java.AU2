package ru.spbau.javacourse.torrent.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import lombok.extern.java.Log;
import ru.spbau.javacourse.torrent.tracker.Tracker;
import ru.spbau.javacourse.torrent.utils.GlobalConstants;

import java.io.IOException;
import java.util.logging.Level;

@Log
public class  TrackerGUI {
    private Stage stage;
    private TrackerScene trackerScene = new TrackerScene();

    public TrackerGUI(Stage primaryStage) {
        stage = primaryStage;
        stage.setScene(trackerScene);
        addExitLogic();
    }

    public void show() {
        this.stage.show();
    }

    private void addExitLogic() {
        stage.setOnCloseRequest(event -> {
            try {
                trackerScene.tracker.stop();
                System.exit(0);
            } catch (IOException | InterruptedException e) {
                log.log(Level.WARNING, "Error on exit!");
                log.log(Level.WARNING, e.getMessage());
            }
        });
    }

    private class TrackerScene extends Scene {
        private final Button startButton;
        private Tracker tracker = new Tracker();

        private TrackerScene() {
            super(new GridPane(), 300, 200);

            final GridPane grid = (GridPane) getRoot();
            grid.setAlignment(Pos.CENTER);
            grid.setHgap(15);
            grid.setVgap(15);
            grid.setPadding(new Insets(25, 25, 25, 25));

            startButton = createConnectButton();
            grid.add(startButton, 0, 0);
        }

        private Button createConnectButton() {
            final Button connectButton = new Button("Start tracker");
            connectButton.setOnMouseClicked(event -> {
                try {
                    tracker.start(GlobalConstants.TRACKER_PORT);
                } catch (IOException e) {
                    SharedGUI.createWarningDialog(stage, "Couldn't start server!");
                    return;
                }
                final GridPane grid = (GridPane) getRoot();
                Label label = new Label("Started");
                grid.add(label, 0, 1);
            });

            return connectButton;
        }
    }
}
