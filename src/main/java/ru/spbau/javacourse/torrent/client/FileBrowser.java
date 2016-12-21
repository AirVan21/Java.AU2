package ru.spbau.javacourse.torrent.client;

import lombok.extern.java.Log;
import ru.spbau.javacourse.torrent.database.ClientDataBase;
import ru.spbau.javacourse.torrent.database.enity.ClientFileRecord;
import ru.spbau.javacourse.torrent.utils.GlobalConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * FileBrowser class collects information about sharing files
 */
@Log
public class FileBrowser {
    private final ClientDataBase db = new ClientDataBase(GlobalConstants.CLIENT_DB_NAME);
    private final String downloadDirectory;

    public FileBrowser(String downloadDirectory) {
        this.downloadDirectory = downloadDirectory;
    }

    public List<ClientFileRecord> getPublishedFileRecords() {
        return db.getPublishedSharedFiles();
    }

    public void addLocalFile(String pathToFile, long fileSize) {
        final boolean isPublished = false;
        db.saveFileRecord(new ClientFileRecord(pathToFile, fileSize, new ArrayList<>(), isPublished));
    }

    public void publishLocalFile(String pathToFile, int fileId) {
        final List<ClientFileRecord> records = db.getFileRecords("fileName", pathToFile);
        if (records.size() != 1) {
            log.log(Level.WARNING, "Database has collision!");
            return;
        }
        final ClientFileRecord targetRecord = records.get(0);
        db.updateFileRecord(targetRecord, "fileServerId", fileId);
        db.updateFileRecord(targetRecord, "isPublished", true);
    }
}
