package ru.spbau.javacourse.ftp.server;

import org.junit.Test;
import ru.spbau.javacourse.ftp.client.Client;

import static org.mockito.Mockito.*;

/**
 * Mock tests for Sever class
 */
public class ServerTest {
    private final static int PORT = 8841;

    @Test
    public void handle() throws Exception {
        Server mockedServer = mock(Server.class);
        mockedServer.start(PORT);

        final Client client = new Client("localhost", PORT);
        client.connect();
        client.disconnect();

        mockedServer.stop();

        verify(mockedServer).start(PORT);
        verify(mockedServer).handle();
        verify(mockedServer).stop();
    }

}