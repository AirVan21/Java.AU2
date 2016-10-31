package ru.spbau.javacourse.ftp.client;


import static org.junit.Assert.*;
import org.junit.Test;
import ru.spbau.javacourse.ftp.server.Server;
import ru.spbau.javacourse.ftp.utils.FileManager;
import ru.spbau.javacourse.ftp.utils.FolderEntity;

import java.io.File;
import java.util.List;
import java.util.Optional;

/**
 * Simple tests for client Side
 */
public class ClientTest {
    private final static int PORT = 8841;
    private final static String TEST_DIR = "src/test/resources/";
    private final static String TEST_FILE = "src/test/resources/a.txt";
    private final static String TEST_FILE_NAME = "a.txt";
    private final static String TEST_RESULT_FILE = "src/test/resources/folder/result.txt";

    @Test
    public void executeListRequest() throws Exception {
        final Server server = new Server();
        server.start(PORT);

        final Client client = new Client("localhost", PORT);
        client.connect();

        List<FolderEntity> files = client.executeListRequest(TEST_DIR);
        final int folderAmount = 1;
        final int filesAmount = 2;

        assertFalse(files.isEmpty());
        assertEquals(filesAmount + folderAmount, files.size());
        assertEquals(folderAmount, files.stream().filter(FolderEntity::isFolder).count());
        assertEquals(filesAmount, files.stream().filter(item -> !item.isFolder()).count());
        assertTrue(files.stream()
                .filter(item -> item.getName().equals(TEST_FILE_NAME))
                .findAny()
                .isPresent());

        client.disconnect();
        server.stop();
    }

    @Test
    public void executeListRequestWrong() throws Exception {
        final Server server = new Server();
        server.start(PORT);

        final Client client = new Client("localhost", PORT);
        client.connect();

        List<FolderEntity> files = client.executeListRequest("./wrong_dir");
        assertTrue(files.isEmpty());

        client.disconnect();
        server.stop();
    }

    @Test
    public void executeGetRequest() throws Exception {
        final Server server = new Server();
        server.start(PORT);

        final Client client = new Client("localhost", PORT);
        client.connect();

        final File testFile = new File(TEST_RESULT_FILE);
        final Optional<String> fileContent = FileManager.readFile(TEST_FILE);

        client.executeGetRequest(TEST_FILE, testFile);
        final Optional<String> downloadedContent = FileManager.readFile(TEST_RESULT_FILE);

        assertTrue(fileContent.isPresent());
        assertTrue(downloadedContent.isPresent());
        assertEquals(fileContent.get(), downloadedContent.get());

        client.disconnect();
        server.stop();
    }
}