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

    public void addLocalFile(String pathToFile) {
        final File file = new File(pathToFile);
        final boolean isPublished = false;
        if (file.exists() && !file.isDirectory()) {
            db.saveFileRecord(new ClientFileRecord(file.getAbsolutePath(), file.length(), new ArrayList<>(), isPublished));
        } else {
            log.log(Level.WARNING, "Invalid file path = " + pathToFile);
        }
    }
}
