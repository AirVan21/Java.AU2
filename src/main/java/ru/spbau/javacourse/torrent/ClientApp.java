package ru.spbau.javacourse.torrent;


import javafx.application.Application;
import javafx.stage.Stage;
import lombok.extern.java.Log;
import ru.spbau.javacourse.torrent.gui.ClientGUI;

import java.util.logging.Level;

/**
 * Gui Application for Torrent Client
 */
@Log
public class ClientApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        log.log(Level.INFO, "Starting client application...");

        final ClientGUI gui = new ClientGUI(primaryStage);
        gui.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
