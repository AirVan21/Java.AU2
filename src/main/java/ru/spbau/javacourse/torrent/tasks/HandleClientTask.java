package ru.spbau.javacourse.torrent.tasks;

import lombok.extern.java.Log;
import ru.spbau.javacourse.torrent.client.DownloadManager;
import ru.spbau.javacourse.torrent.client.FileBrowser;
import ru.spbau.javacourse.torrent.commands.ClientRequest;
import ru.spbau.javacourse.torrent.database.enity.ClientFileRecord;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

@Log
public class HandleClientTask extends HandleTask {
    private final FileBrowser browser;

    public HandleClientTask(Socket connection, FileBrowser browser) throws IOException {
        super(connection);
        this.browser = browser;
    }

    @Override
    protected void executeRequest(byte requestId) throws IOException {
        log.log(Level.INFO, "Executes request = " + Byte.toString(requestId));

        final Optional<String> host = getHostFromAddress(taskSocket.getRemoteSocketAddress().toString());
        if (!host.isPresent()) {
            return;
        }

        switch (requestId) {
            case ClientRequest.GET_STAT_REQUEST:
                int fileId = input.readInt();
                List<ClientFileRecord> records = browser.getClientFileRecords("fileServerId", fileId);
                if (records.size() != 1) {
                    log.log(Level.WARNING, "ClientFileRecord id collision!");
                    output.writeInt(0);
                    return;
                }
                ClientFileRecord record = records.get(0);
                output.writeInt(record.getAvailableChunks().size());
                for (Integer chunkId : record.getAvailableChunks()) {
                    output.writeInt(chunkId);
                }
                break;
            case ClientRequest.GET_FILE_REQUEST:
                fileId = input.readInt();
                int chunkId = input.readInt();
                records = browser.getClientFileRecords("fileServerId", fileId);
                if (records.size() != 1) {
                    log.log(Level.WARNING, "ClientFileRecord id collision!");
                    output.writeInt(0);
                    return;
                }
                record = records.get(0);
                String path = host.get() + File.separator + record.getFileName();
                DownloadManager.writeFileChunk(path, chunkId, input);
                break;
        }
        output.flush();
    }
}
