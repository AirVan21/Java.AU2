package ru.spbau.javacourse.torrent.client;

import lombok.extern.java.Log;
import ru.spbau.javacourse.torrent.database.ClientDataBase;
import ru.spbau.javacourse.torrent.database.enity.ClientFileRecord;
import ru.spbau.javacourse.torrent.database.enity.SimpleFileRecord;
import ru.spbau.javacourse.torrent.utils.GlobalConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * FileBrowser class collects information about sharing files
 */
@Log
public class FileBrowser {
    private final ClientDataBase db = new ClientDataBase(GlobalConstants.CLIENT_DB_NAME);
    private final short port;
    private static final int DEFAULT_FILE_SERVER_ID = 0;

    public FileBrowser(short port) {
        this.port = port;
    }

    public synchronized void addLocalFile(String fileName, String filePath, long fileSize) {
        log.log(Level.INFO, "addFutureFile: " + fileName);

        final boolean isPublished = false;
        db.saveFileRecord(new ClientFileRecord(fileName, filePath, fileSize, port ,makeFileChunks(fileSize), isPublished, DEFAULT_FILE_SERVER_ID));
    }

    public synchronized String addFutureFile(SimpleFileRecord record) {
        log.log(Level.INFO, "addFutureFile: " + record.getName());

        final String filePath = GlobalConstants.DOWNLOAD_DIR + port + File.separator + record.getName();
        final boolean isPublished = false;
        db.saveFileRecord(new ClientFileRecord(record.getName(), filePath, record.getSize(), port, new ArrayList<>(), isPublished, record.getId()));

        return filePath;
    }

    public synchronized void addAvailableChunk(int fileId, int chunkId) {
        log.log(Level.INFO, "add chunk = " + chunkId + " to fileId = " + fileId);

        final List<ClientFileRecord> records = getClientFileRecords("fileServerId", fileId);
        if (records.size() != 1) {
            log.log(Level.WARNING, "Database has collision!");
            return;
        }
        final ClientFileRecord targetRecord = records.get(0);
        List<Integer> chunks = targetRecord.getAvailableChunks();
        chunks.add(chunkId);
        db.updateFileRecord(targetRecord, "availableChunks", chunks);
        if (!targetRecord.isPublished()) {
            db.updateFileRecord(targetRecord, "isPublished", true);
        }
    }

    public synchronized void publishLocalFile(String fileName, int fileId) {
        log.log(Level.INFO, "publish " + fileName + "with id = " + Integer.toString(fileId));

        final List<ClientFileRecord> records = db.getFileRecords("fileName", fileName);
        if (records.size() != 1) {
            log.log(Level.WARNING, "Database has collision!");
            return;
        }
        final ClientFileRecord targetRecord = records.get(0);
        db.updateFileRecord(targetRecord, "fileServerId", fileId);
        db.updateFileRecord(targetRecord, "isPublished", true);
    }

    public <T> List<ClientFileRecord> getClientFileRecords(String fieldName, T value) {
        final List<ClientFileRecord> records = db.getFileRecords(fieldName, value);
        return records
                .stream()
                .filter(item -> item.getPort() == port)
                .collect(Collectors.toList());
    }

    public List<ClientFileRecord> getPublishedFileRecords() {
        final List<ClientFileRecord> records = db.getFileRecords("isPublished", true);
        return records
                .stream()
                .filter(item -> item.getPort() == port)
                .collect(Collectors.toList());
    }

    public void dropCollection(Class source) {
        db.dropCollection(source);
    }

    public void dropDatabase() {
        db.dropDatabase();
    }

    private static List<Integer> makeFileChunks(long fileSize) {
        final List<Integer> chunks = new ArrayList<>();
        long amountOfChunks = (fileSize % GlobalConstants.CHUNK_SIZE == 0)
                ? fileSize / GlobalConstants.CHUNK_SIZE
                : fileSize / GlobalConstants.CHUNK_SIZE + 1;
        for (int i = 0; i < amountOfChunks; ++i) {
            chunks.add(i);
        }

        return chunks;
    }
}
