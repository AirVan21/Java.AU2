package ru.spbau.javacourse.torrent;

import javafx.application.Application;
import javafx.stage.Stage;
import lombok.extern.java.Log;
import ru.spbau.javacourse.torrent.gui.TrackerGUI;

import java.util.logging.Level;

@Log
public class TrackerApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        log.log(Level.INFO, "Starting tracker application ...");

        final TrackerGUI gui = new TrackerGUI(primaryStage);
        gui.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
