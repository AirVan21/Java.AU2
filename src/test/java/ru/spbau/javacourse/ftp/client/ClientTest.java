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
    private final static int PORT = 8842;
    private final static String HOST_NAME = "localhost";
    private final static String TEST_DIR = "src/test/resources/";
    private final static String TEST_FILE_PATH = "src/test/resources/a.txt";
    private final static String TEST_FILE_NAME = "a.txt";
    private final static String TEST_RESULT_FILE = "src/test/resources/folder/result.txt";

    @Test
    public void executeListRequest() throws Exception {
        final Server server = new Server();
        server.start(PORT);

        final Client client = new Client(HOST_NAME, PORT);
        client.connect();

        List<FolderEntity> files = client.executeListRequest(TEST_DIR);
        final int FOLDER_AMOUNT = 1;
        final int FILES_AMOUNT = 2;

        assertFalse(files.isEmpty());
        assertEquals(FILES_AMOUNT + FOLDER_AMOUNT, files.size());
        assertEquals(FOLDER_AMOUNT, files.stream().filter(FolderEntity::isFolder).count());
        assertEquals(FILES_AMOUNT, files.stream().filter(item -> !item.isFolder()).count());
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

        final Client client = new Client(HOST_NAME, PORT);
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

        final Client client = new Client(HOST_NAME, PORT);
        client.connect();

        final File testFile = new File(TEST_RESULT_FILE);
        final Optional<String> fileContent = FileManager.readFile(TEST_FILE_PATH);

        client.executeGetRequest(TEST_FILE_PATH, testFile);
        final Optional<String> downloadedContent = FileManager.readFile(TEST_RESULT_FILE);

        assertTrue(fileContent.isPresent());
        assertTrue(downloadedContent.isPresent());
        assertEquals(fileContent.get(), downloadedContent.get());

        client.disconnect();
        server.stop();
    }

    @Test
    public void executeGetRequestWrong() throws Exception {
        final Server server = new Server();
        server.start(PORT);

        final Client client = new Client(HOST_NAME, PORT);
        client.connect();

        final File testFile = new File(TEST_RESULT_FILE);
        client.executeGetRequest("src/wrong.txt", testFile);

        final Optional<String> downloadedContent = FileManager.readFile(TEST_RESULT_FILE);
        System.out.println(downloadedContent.get().isEmpty());

        client.disconnect();
        server.stop();
    }
}