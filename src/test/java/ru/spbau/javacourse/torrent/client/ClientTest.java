package ru.spbau.javacourse.torrent.client;

import org.junit.Test;
import ru.spbau.javacourse.torrent.tracker.Tracker;
import ru.spbau.javacourse.torrent.utils.GlobalConstants;

public class ClientTest {
    private final static String HOST_NAME = "localhost";
    private final static short SERVER_PORT = GlobalConstants.TRACKER_PORT;

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
    public void doUpdate() throws Exception {
        final Tracker tracker = new Tracker();
        tracker.start(SERVER_PORT);

        final Client client = new Client(HOST_NAME, SERVER_PORT);
        client.connectToServer();

        client.doUpdate();
        client.doUpdate();
        client.doUpdate();

        client.disconnectFromServer();
        tracker.stop();
    }
}