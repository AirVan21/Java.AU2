package ru.spbau.utility;

import org.bson.types.ObjectId;
import ru.spbau.db.DataBase;
import ru.spbau.db.entity.Commit;
import ru.spbau.db.entity.File;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Status manager collects information of files state in the repo
 */
public class StatusManager {
    /**
     * Gets files which we added between two commits
     * @param current ccommit which describes last committed state
     * @param previous commit which describes previous committed state
     * @return Set of paths of added files
     */
    public static Set<String> getAddedFiles(Commit current, Commit previous) {
        return current.storageTable
                .keySet()
                .stream()
                .filter(item -> !previous.storageTable.containsKey(item))
                .collect(Collectors.toSet());
    }

    /**
     * Gets files which we deleted between two commits
     * @param current commit which describes last committed state
     * @param previous commit which describes previous committed state
     * @return Set of paths of deleted files
     */
    public static Set<String> getDeletedFiles(Commit current, Commit previous) {
        return previous.storageTable
                .keySet()
                .stream()
                .filter(item -> !current.storageTable.containsKey(item))
                .collect(Collectors.toSet());
    }

    /**
     * Gets files which we modified since last
     * @param current commit which describes last committed state
     * @param dirPath path to working dir
     * @param database database object for collecting info
     * @return Set of paths of modified files
     */
    public static Set<String> getModifiedFiles(Commit current, String dirPath, DataBase database) {
        final Set<String> trackedFiles = new HashSet<>(current.storageTable.keySet());
        final boolean isRecursive = true;
        final Set<String> availableFiles = FileManager.listFiles(dirPath, isRecursive);

        return trackedFiles
                .stream()
                .filter(availableFiles::contains)
                .filter(item -> {
                    ObjectId storedFileId = current.storageTable.get(item);
                    List<File> file = database.getFile(storedFileId);
                    // Skips bad cases
                    if (file.isEmpty()) {
                        return false;
                    }
                    Optional<String> currentText = FileManager.readFile(item);
                    if (!currentText.isPresent()) {
                        return false;
                    }
                    // Save filename if file was modified since last commit
                    return !currentText.get().equals(file.get(0).getText());
                })
                .collect(Collectors.toSet());
    }
}
