package ru.spbau.javacourse.torrent.tasks;

import lombok.extern.java.Log;
import ru.spbau.javacourse.torrent.client.DownloadManager;
import ru.spbau.javacourse.torrent.client.FileBrowser;
import ru.spbau.javacourse.torrent.commands.ClientRequest;
import ru.spbau.javacourse.torrent.database.enity.ClientFileRecord;

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
        
        int fileId = input.readInt();
        Optional<ClientFileRecord> record = getSingleFileRecordById(fileId);
        if (!record.isPresent()) {
            log.log(Level.WARNING, "ClientFileRecord id collision!");
            output.writeInt(0);
            return;
        }

        switch (requestId) {
            case ClientRequest.GET_STAT_REQUEST:
                output.writeInt(record.get().getAvailableChunks().size());
                for (Integer chunkId : record.get().getAvailableChunks()) {
                    output.writeInt(chunkId);
                }
                break;
            case ClientRequest.GET_FILE_REQUEST:
                int chunkId = input.readInt();
                DownloadManager.readFileChunk(record.get().getFilePath(), chunkId, output);
                break;
        }

        // Flushed response
        output.flush();
    }

    private Optional<ClientFileRecord> getSingleFileRecordById(int fileId) {
        final String FILE_ID_FIELD = "fileServerId";
        List<ClientFileRecord> records = browser.getClientFileRecords(FILE_ID_FIELD, fileId);

        return records.size() == 1 ? Optional.of(records.get(0)) : Optional.empty();
    }
}
