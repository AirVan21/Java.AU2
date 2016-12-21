package ru.spbau.javacourse.torrent.client;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import static org.junit.Assert.*;

import org.junit.rules.TemporaryFolder;
import ru.spbau.javacourse.torrent.database.enity.ServerFileRecord;
import ru.spbau.javacourse.torrent.database.enity.User;
import ru.spbau.javacourse.torrent.tracker.Tracker;
import ru.spbau.javacourse.torrent.utils.GlobalConstants;

import java.io.File;
import java.io.FileOutputStream;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class ClientTest {
    private final static String HOST_NAME = "localhost";
    private final static short SERVER_PORT = GlobalConstants.TRACKER_PORT;

    @Rule
    public final TemporaryFolder temporaryWorkDir = new TemporaryFolder();

    @Before
    public void setWorkDir() {
        System.setProperty("user.dir", temporaryWorkDir.getRoot().getAbsolutePath());
    }

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

    @Test
    public void doUpload() throws Exception {
        final Tracker tracker = new Tracker();
        Tracker spyTracker = spy(tracker);
        spyTracker.start(SERVER_PORT);

        final Client client = new Client(HOST_NAME, GlobalConstants.CLIENT_PORT_FST);
        Client spyClient = spy(client);
        spyClient.connectToServer();

        final File file = temporaryWorkDir.newFile("TestFile");
        final FileOutputStream stream = new FileOutputStream(file);
        byte[] buffer = new byte[128];
        stream.write(buffer);
        stream.flush();
        stream.close();

        spyClient.doUpload(file.getAbsolutePath());

        spyClient.disconnectFromServer();
        spyTracker.stop();

        verify(spyClient).doUpload(file.getAbsolutePath());
        verify(spyTracker).addServerFileRecord(any(ServerFileRecord.class));
        assertEquals(1, spyClient.getFileRecords("fileName", file.getAbsolutePath()).size());
        assertEquals(1, spyTracker.getServerFileRecords().size());
    }
}