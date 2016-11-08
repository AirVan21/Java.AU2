package ru.spbau.javacourse.torrent.client;

import org.junit.Test;
import ru.spbau.javacourse.torrent.tracker.Tracker;

/**
 *
 */
public class ClientTest {
    private final static short SERVER_PORT = 8081;
    private final static String HOST_NAME = "localhost";

    @Test
    public void connectToServer() throws Exception {
        final Tracker tracker = new Tracker();
        tracker.start(SERVER_PORT);

        final Client client = new Client(HOST_NAME, SERVER_PORT);
        client.connectToServer();

        client.disconnectFromServer();
        tracker.stop();
    }

    @Test
    public void disconnectFromServer() throws Exception {

    }

    @Test
    public void doUpdate() throws Exception {
        final Tracker tracker = new Tracker();
        tracker.start(SERVER_PORT);

        final Client client = new Client(HOST_NAME, SERVER_PORT);
        client.connectToServer();

        client.doUpdate();

        client.disconnectFromServer();
        tracker.stop();
    }

}