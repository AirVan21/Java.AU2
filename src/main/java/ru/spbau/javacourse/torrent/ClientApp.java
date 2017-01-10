package ru.spbau.javacourse.torrent;


import javafx.application.Application;
import javafx.stage.Stage;
import lombok.extern.java.Log;
import ru.spbau.javacourse.torrent.client.Client;
import ru.spbau.javacourse.torrent.gui.TorrentGUI;
import ru.spbau.javacourse.torrent.utils.GlobalConstants;

import java.util.List;
import java.util.logging.Level;

@Log
public class ClientApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        log.log(Level.INFO, "Starting application...");

        final List<String> args = getParameters().getRaw();
        if (args.size() < 1) {
            log.log(Level.WARNING, "Invalid number of input arguments!");
        }

        short port = 8080;
        try {
            port = Short.parseShort(args.get(0));
        } catch (NumberFormatException e) {
            log.log(Level.WARNING, "Failed to parse input port!");
            log.log(Level.WARNING, e.getMessage());
        }
        log.log(Level.INFO, "Starting Application on port = " + Integer.toString(port));

        final Client client = new Client(GlobalConstants.DEFAULT_HOST, port);
        final TorrentGUI gui = new TorrentGUI(primaryStage, client);
        gui.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
