package ru.spbau.javacourse.ftp.client;



import org.junit.Test;
import ru.spbau.javacourse.ftp.server.Server;
import ru.spbau.javacourse.ftp.utils.FolderEntity;

import java.io.File;
import java.util.List;

/**
 * Simple tests for client Side
 */
public class ClientTest {
    private final static int PORT = 8841;

    @Test
    public void executeListRequest() throws Exception {
        final Server server = new Server();
        server.start(PORT);

        final Client client = new Client("localhost", PORT);
        client.connect();

        List<FolderEntity> files = client.executeListRequest("./");

        client.disconnect();
        server.stop();
    }

    @Test
    public void executeGetRequest() throws Exception {
        final Server server = new Server();
        server.start(PORT);

        final Client client = new Client("localhost", PORT);
        client.connect();

        File testFile = new File("text.txt");
        client.executeGetRequest("./pom.xml", testFile);

        client.disconnect();
        server.stop();
    }
}