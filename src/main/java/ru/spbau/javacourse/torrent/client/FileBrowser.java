package ru.spbau.javacourse.torrent.client;

import lombok.extern.java.Log;
import ru.spbau.javacourse.torrent.database.ClientDataBase;
import ru.spbau.javacourse.torrent.database.enity.ClientFileRecord;
import ru.spbau.javacourse.torrent.database.enity.ServerFileRecord;
import ru.spbau.javacourse.torrent.database.enity.SimpleFileRecord;
import ru.spbau.javacourse.torrent.utils.GlobalConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

/**
 * FileBrowser class collects information about sharing files
 */
@Log
public class FileBrowser {
    private final ClientDataBase db = new ClientDataBase(GlobalConstants.CLIENT_DB_NAME);
    private static final int DEFAULT_FILE_SERVER_ID = 0;

    public FileBrowser() {}

    public synchronized void addLocalFile(String fileName, long fileSize) {
        log.log(Level.INFO, "addFutureFile: " + fileName);

        final boolean isPublished = false;
        db.saveFileRecord(new ClientFileRecord(fileName, fileSize, makeFileChunks(fileSize), isPublished, DEFAULT_FILE_SERVER_ID));
    }

    public synchronized void addFutureFile(SimpleFileRecord record) {
        log.log(Level.INFO, "addFutureFile: " + record.getName());

        final boolean isPublished = false;
        db.saveFileRecord(new ClientFileRecord(record.getName(), record.getSize(), new ArrayList<>(), isPublished, DEFAULT_FILE_SERVER_ID));
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
        return db.getFileRecords(fieldName, value);
    }

    public List<ClientFileRecord> getPublishedFileRecords() {
        return db.getPublishedSharedFiles();
    }

    public void dropDatabase() {
        db.dropDatabase();
    }

    public void dropCollection(Class source) {
        db.dropCollection(source);
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
