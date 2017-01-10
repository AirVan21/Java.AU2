package ru.spbau.javacourse.torrent.client;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static java.lang.Thread.sleep;
import static org.junit.Assert.*;

import org.junit.rules.TemporaryFolder;
import ru.spbau.javacourse.torrent.database.enity.ClientFileRecord;
import ru.spbau.javacourse.torrent.database.enity.ServerFileRecord;
import ru.spbau.javacourse.torrent.database.enity.SimpleFileRecord;
import ru.spbau.javacourse.torrent.database.enity.User;
import ru.spbau.javacourse.torrent.tracker.Tracker;
import ru.spbau.javacourse.torrent.utils.GlobalConstants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class ClientServerTest {
    private final static String HOST_NAME = "127.0.0.1";
    private final static String TEST_FILE_FST = "test_file_fst";
    private final static String TEST_FILE_SND = "test_file_snd";
    private final static short SERVER_PORT = GlobalConstants.TRACKER_PORT;

    @Rule
    public final TemporaryFolder temporaryWorkDir = new TemporaryFolder(new File(GlobalConstants.DOWNLOAD_DIR));

    @Test
    public void connectToServerTest() throws Exception {
        Tracker spyTracker = spy(new Tracker());
        spyTracker.start(SERVER_PORT);

        final Client spyClient = runSpyClient (HOST_NAME, GlobalConstants.CLIENT_PORT_FST);

        sleep(100);

        spyClient.disconnectFromServer();
        spyTracker.stop();

        verify(spyTracker).start(SERVER_PORT);
        verify(spyClient).connectToServer();
        verify(spyClient).disconnectFromServer();
        verify(spyTracker).stop();
    }

    @Test
    public void doUpdateTest() throws Exception {
        Tracker spyTracker = spy(new Tracker());
        spyTracker.start(SERVER_PORT);

        // Create client and run simple update
        Client spyClient = runSpyClient(HOST_NAME, GlobalConstants.CLIENT_PORT_FST);

        // doUpdate is done by timer, wait for Update
        sleep(100);

        spyClient.disconnectFromServer();
        spyTracker.stop();

        // Checks that  update was called
        verify(spyClient).doUpdate();
    }

    @Test
    public void doUploadTest() throws Exception {
        final Tracker tracker = new Tracker();
        Tracker spyTracker = spy(tracker);
        spyTracker.start(SERVER_PORT);

        Client spyClient = runSpyClient(HOST_NAME, GlobalConstants.CLIENT_PORT_FST);

        // Creates and uploads file
        final File file = createTemporaryFile(TEST_FILE_FST);
        spyClient.doUpload(file.getAbsolutePath());

        // doUpload
        sleep(100);

        // Checks that file was written to databases
        assertEquals(1, spyClient.getFileRecords("fileName", file.getName()).size());
        assertEquals(1, spyTracker.getServerFileRecords().size());

        spyClient.disconnectFromServer();
        spyTracker.stop();

        // Checks calls
        verify(spyClient).doUpload(file.getAbsolutePath());
        verify(spyTracker).addServerFileRecord(any(ServerFileRecord.class));
    }

    @Test
    public void doListTest() throws Exception {
        Tracker spyTracker = spy(new Tracker());
        spyTracker.start(SERVER_PORT);

        // Creates first client
        Client spyClientFst = runSpyClient(HOST_NAME, GlobalConstants.CLIENT_PORT_FST);

        // Creates second client
        Client spyClientSnd = runSpyClient(HOST_NAME, GlobalConstants.CLIENT_PORT_SND);

        // First client uploads file
        final File file = createTemporaryFile(TEST_FILE_FST);
        spyClientFst.doUpload(file.getAbsolutePath());

        // doUpload
        sleep(100);

        // Second client asks List()
        Optional<List<SimpleFileRecord>> answer = spyClientSnd.doList();

        // Check that uploaded file is visible for second client
        assertTrue(answer.isPresent());
        assertEquals(1, answer.get().size());
        SimpleFileRecord loadedRecord = answer.get().get(0);
        assertEquals(file.getName(), loadedRecord.getName());
        assertEquals(file.length(), loadedRecord.getSize());

        // Check that local id matches with downloaded info id
        List<ClientFileRecord> localRecords = spyClientFst.getFileRecords("fileName", file.getName());
        assertEquals(1, localRecords.size());
        assertEquals(file.getName(), localRecords.get(0).getFileName());
        assertEquals(file.length(), localRecords.get(0).getFileSize());

        // Compares ids
        assertEquals(localRecords.get(0).getFileServerId(), loadedRecord.getId());

        // Stops all
        spyClientFst.disconnectFromServer();
        spyClientSnd.disconnectFromServer();
        spyTracker.stop();
    }

    @Test
    public void doSourcesTest() throws Exception {
        // Creates tracker
        Tracker spyTracker = spy(new Tracker());
        spyTracker.start(SERVER_PORT);

        // Creates first client
        Client spyClientFst = runSpyClient(HOST_NAME, GlobalConstants.CLIENT_PORT_FST);

        // Creates second client
        Client spyClientSnd = runSpyClient(HOST_NAME, GlobalConstants.CLIENT_PORT_SND);

        // First client uploads file
        final File file = createTemporaryFile(TEST_FILE_FST);
        spyClientFst.doUpload(file.getAbsolutePath());

        // doUpload
        sleep(100);

        // Second client asks List()
        Optional<List<SimpleFileRecord>> answer = spyClientSnd.doList();
        assertEquals(1, answer.get().size());
        SimpleFileRecord loadedRecord = answer.get().get(0);

        // Asks user who has file
        Optional<List<User>> seeds = spyClientSnd.doSources(loadedRecord.getId());
        assertTrue(seeds.isPresent());
        assertEquals(1, seeds.get().size());
        User user = seeds.get().get(0);

        assertEquals(HOST_NAME, user.getHost());
        assertEquals(GlobalConstants.CLIENT_PORT_FST, user.getPort());

        // Stops all
        spyClientFst.disconnectFromServer();
        spyClientSnd.disconnectFromServer();
        spyTracker.stop();
    }

    @Test
    public void doStatTest() throws IOException, InterruptedException {
        // Creates tracker
        Tracker spyTracker = spy(new Tracker());
        spyTracker.start(SERVER_PORT);

        // Creates first client
        Client spyClientFst = runSpyClient(HOST_NAME, GlobalConstants.CLIENT_PORT_FST);

        // Creates second client
        Client spyClientSnd = runSpyClient(HOST_NAME, GlobalConstants.CLIENT_PORT_SND);

        // First client uploads file
        final File file = createTemporaryFile(TEST_FILE_FST);
        spyClientFst.doUpload(file.getAbsolutePath());

        // doUpload
        sleep(100);

        // Second client asks List()
        Optional<List<SimpleFileRecord>> answer = spyClientSnd.doList();
        assertEquals(1, answer.get().size());
        SimpleFileRecord loadedRecord = answer.get().get(0);

        Optional<Map<User, List<Integer>>> stat = spyClientSnd.doStat(loadedRecord.getId());
        assertTrue(stat.isPresent());
        assertEquals(1, stat.get().size());
        User user = stat.get().entrySet().stream()
                .findFirst().get().getKey();
        List<Integer> chunks = stat.get().entrySet().stream()
                .findFirst().get().getValue();

        // Validate user and chunks
        assertEquals(GlobalConstants.CLIENT_PORT_FST, user.getPort());
        assertEquals(file.length() / GlobalConstants.CHUNK_SIZE, chunks.size());
        assertTrue(chunks.contains(0));
        assertTrue(chunks.contains(4));

        // Stops all
        spyClientFst.disconnectFromServer();
        spyClientSnd.disconnectFromServer();
        spyTracker.stop();
    }

    @Test
    public void doGetSimple() throws IOException, InterruptedException {
        // Creates tracker
        Tracker spyTracker = spy(new Tracker());
        spyTracker.start(SERVER_PORT);

        // Creates first client
        Client spyClientFst = runSpyClient(HOST_NAME, GlobalConstants.CLIENT_PORT_FST);

        // Creates second client
        Client spyClientSnd = runSpyClient(HOST_NAME, GlobalConstants.CLIENT_PORT_SND);

        // First client uploads file
        final File file = createTemporaryFile(TEST_FILE_FST);
        spyClientFst.doUpload(file.getAbsolutePath());

        // doUpload
        sleep(100);

        // Second client asks List()
        Optional<List<SimpleFileRecord>> answer = spyClientSnd.doList();
        assertEquals(1, answer.get().size());
        SimpleFileRecord uploadedRecord = answer.get().get(0);

        // Second client asks Get()
        assertTrue(spyClientSnd.doGet(uploadedRecord.getId()));
        List<ClientFileRecord> downloadedRecords = spyClientSnd.getFileRecords("fileName", uploadedRecord.getName());
        assertEquals(1, downloadedRecords.size());
        ClientFileRecord targetRecord = downloadedRecords.get(0);

        assertEquals(uploadedRecord.getName(), targetRecord.getFileName());
        assertEquals(uploadedRecord.getSize(), targetRecord.getFileSize());
        assertEquals(uploadedRecord.getId(), targetRecord.getFileServerId());

        File downloadedFile = new File(targetRecord.getFilePath());
        assertEquals(file.length(), downloadedFile.length());
        assertTrue(FileUtils.contentEquals(file, downloadedFile));

        // Stops all
        spyClientFst.disconnectFromServer();
        spyClientSnd.disconnectFromServer();
        spyTracker.stop();
    }

    @Test
    public void doGetTest() throws IOException, InterruptedException {
        // Creates tracker
        Tracker spyTracker = spy(new Tracker());
        spyTracker.start(SERVER_PORT);

        // Creates first client
        Client spyClientFst = runSpyClient(HOST_NAME, GlobalConstants.CLIENT_PORT_FST);

        // Creates second client
        Client spyClientSnd = runSpyClient(HOST_NAME, GlobalConstants.CLIENT_PORT_SND);

        // Creates third client
        Client spyClientThd = runSpyClient(HOST_NAME, GlobalConstants.CLIENT_PORT_THD);

        // First client uploads file
        final File file = createTemporaryFile(TEST_FILE_FST);
        spyClientFst.doUpload(file.getAbsolutePath());

        // doUpload
        sleep(100);

        // Second client asks List() - only uploaded file is available
        Optional<List<SimpleFileRecord>> answer = spyClientSnd.doList();
        assertEquals(1, answer.get().size());
        SimpleFileRecord uploadedRecord = answer.get().get(0);

        // Second client asks Stat() - only uploaded available as a seed
        Optional<Map<User, List<Integer>>> stat = spyClientSnd.doStat(uploadedRecord.getId());
        assertTrue(stat.isPresent());
        assertEquals(1, stat.get().size());

        // Checks that only first client is a seed
        assertTrue(stat.get()
                .keySet()
                .iterator()
                .next()
                .getPort() == GlobalConstants.CLIENT_PORT_FST);

        // Second client asks Get() - asserts that downloaded file with same id
        assertTrue(spyClientSnd.doGet(uploadedRecord.getId()));
        List<ClientFileRecord> downloadedRecords = spyClientSnd.getFileRecords("fileName", uploadedRecord.getName());
        assertEquals(1, downloadedRecords.size());
        ClientFileRecord downloadedRecord = downloadedRecords.get(0);
        assertEquals(uploadedRecord.getId(), downloadedRecord.getFileServerId());

        // Binary comparison of downloaded files
        File downloadedFile = new File(downloadedRecord.getFilePath());
        assertEquals(file.length(), downloadedFile.length());
        assertTrue(FileUtils.contentEquals(file, downloadedFile));

        // Second client sends update (now he is seed)
        spyClientSnd.doUpdate();

        // Third client asks Stat() (now we have got two seeds)
        stat = spyClientThd.doStat(uploadedRecord.getId());
        assertTrue(stat.isPresent());
        assertEquals(2, stat.get().size());

        // Third client asks Get() - asserts that downloaded file with same id
        assertTrue(spyClientThd.doGet(uploadedRecord.getId()));

        // Checks that third client loaded files
        downloadedRecords = spyClientThd.getFileRecords("fileName", uploadedRecord.getName());
        assertEquals(1, downloadedRecords.size());
        downloadedRecord = downloadedRecords.get(0);
        assertEquals(uploadedRecord.getId(), downloadedRecord.getFileServerId());

        downloadedFile = new File(downloadedRecord.getFilePath());
        assertEquals(file.length(), downloadedFile.length());
        assertTrue(FileUtils.contentEquals(file, downloadedFile));

        // Stops all
        spyClientFst.disconnectFromServer();
        spyClientSnd.disconnectFromServer();
        spyClientThd.disconnectFromServer();
        spyTracker.stop();
    }

    private File createTemporaryFile(String fileName) throws IOException {
        final File file = temporaryWorkDir.newFile(fileName);
        final FileOutputStream stream = new FileOutputStream(file);
        byte[] buffer = new byte[(int) GlobalConstants.CHUNK_SIZE * 5];
        buffer[0] = 1;
        buffer[1] = 2;
        buffer[2] = 3;

        stream.write(buffer);
        stream.flush();
        stream.close();

        return file;
    }

    private Client runSpyClient(String host, short port) throws IOException {
        Client spyClient = spy(new Client(host, port));
        spyClient.clearFileRecords();
        spyClient.connectToServer();

        return spyClient;
    }
}