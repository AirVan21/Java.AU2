package ru.spbau.javacourse.torrent.client;

import ru.spbau.javacourse.torrent.database.DataBase;
import ru.spbau.javacourse.torrent.database.enity.SharedFileRecord;
import ru.spbau.javacourse.torrent.utils.GlobalConstants;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * FileBrowser class collects information about sharing files
 */
public class FileBrowser {
    private final DataBase db = new DataBase(GlobalConstants.CLIENT_DB_NAME);

    public FileBrowser() {}

    public void addLocalSharedFileRecord(String pathToFile) {
        final Path path = Paths.get(pathToFile);
        final File file = new File(pathToFile);
        if (file.exists()) {
            final boolean isPublished = false;
            final List<Boolean> filledChunks = getChunks(file.length())
                    .stream()
                    .map(item -> true)
                    .collect(Collectors.toList());
            db.saveSharedFileRecord(new SharedFileRecord(path.getFileName().toString(), file.length(), filledChunks, isPublished));
        }
    }

    private List<Boolean> getChunks(long fileSize) {
        final List<Boolean> chunks = new ArrayList<>();
        long elementsCount = fileSize / GlobalConstants.CHUNK_SIZE;
        do {
            chunks.add(false);
            elementsCount--;
        } while (elementsCount > 0);

        return chunks;
    }
}
