package ru.spbau.javacourse.torrent.client;

import org.junit.Test;
import static org.junit.Assert.*;

import ru.spbau.javacourse.torrent.database.enity.User;
import ru.spbau.javacourse.torrent.tracker.Tracker;
import ru.spbau.javacourse.torrent.utils.GlobalConstants;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class ClientTest {
    private final static String HOST_NAME = "localhost";
    private final static short SERVER_PORT = GlobalConstants.TRACKER_PORT;

    @Test
    public void connectToServer() throws Exception {
        final Tracker tracker = new Tracker();
        Tracker spyTracker = spy(tracker);
        spyTracker.start(SERVER_PORT);

        final Client client = new Client(HOST_NAME, GlobalConstants.CLIENT_PORT_FST);
        Client spyClient = spy(client);
        spyClient.connectToServer();

        spyClient.disconnectFromServer();
        spyTracker.stop();

        verify(spyTracker).start(SERVER_PORT);
        verify(spyClient).connectToServer();
        verify(spyClient).disconnectFromServer();
        verify(spyTracker).stop();
    }

    @Test
    public void doUpdate() throws Exception {
        final Tracker tracker = new Tracker();
        Tracker spyTracker = spy(tracker);
        spyTracker.start(SERVER_PORT);

        final Client client = new Client(HOST_NAME, GlobalConstants.CLIENT_PORT_FST);
        Client spyClient = spy(client);
        spyClient.connectToServer();
        spyClient.doUpdate();

        spyClient.disconnectFromServer();
        spyTracker.stop();

        verify(spyClient).doUpdate();
        verify(spyTracker).addUserInformation(any(User.class), anySetOf(Integer.class));
        assertEquals(1, spyTracker.getUsers().size());
    }
}