package ru.spbau.javacourse.ftp.client;


import static org.junit.Assert.*;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import ru.spbau.javacourse.ftp.server.Server;
import ru.spbau.javacourse.ftp.utils.FileManager;
import ru.spbau.javacourse.ftp.utils.FolderEntity;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        final Optional<String> fileContent = readFile(TEST_FILE_PATH);

        client.executeGetRequest(TEST_FILE_PATH, testFile);
        final Optional<String> downloadedContent = readFile(TEST_RESULT_FILE);

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
        final Optional<String> fileContent = readFile(TEST_FILE_PATH);
        client.executeGetRequest("src/wrong.txt", testFile);

        final Optional<String> downloadedContent = readFile(TEST_RESULT_FILE);
        assertTrue(fileContent.isPresent());
        assertTrue(downloadedContent.isPresent());
        // Checks that nothing changed
        assertEquals(fileContent.get(), downloadedContent.get());

        client.disconnect();
        server.stop();
    }

    /**
     * Returns file for specified path
     * @param filePath path string
     * @return file if it exists
     */
    private static Optional<File> getFile(String filePath) {
        final Path path = Paths.get(filePath);
        return Files.exists(path) ? Optional.of(new File(filePath)) : Optional.empty();
    }

    /**
     * Reads file to string
     * @param file - text file
     * @return string with file content
     */
    private static Optional<String> readFile(File file) {
        String result;
        try {
            result = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        } catch (IOException exc) {
            return Optional.empty();
        }

        return Optional.of(result);
    }

    /**
     * Reads file in string if file exists
     * @param path path to file
     * @return Optional with file text if file exist, Optional empty elsewhere
     */
    private static Optional<String> readFile(String path) {
        Optional<File> file = getFile(path);
        return file.isPresent() ? readFile(file.get()) : Optional.empty();
    }
}