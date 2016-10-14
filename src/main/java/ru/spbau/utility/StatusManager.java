package ru.spbau.utility;

import ru.spbau.db.entity.Commit;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 */
public class StatusManager {
    public static Set<String> getAddedFiles(Commit current, Commit previous) {
        return current.getStorageTable()
                .keySet()
                .stream()
                .filter(item -> !previous.getStorageTable().containsKey(item))
                .collect(Collectors.toSet());
    }

    public static Set<String> getDeletedFiles(Commit current, Commit previous) {
        return previous.getStorageTable()
                .keySet()
                .stream()
                .filter(item -> !current.getStorageTable().containsKey(item))
                .collect(Collectors.toSet());
    }

    public static Set<String> getModifiedFiles(Commit current, Commit previous) {
        final Set<String> result = new HashSet<>(current.getStorageTable().keySet());
        result.retainAll(previous.getStorageTable().keySet());

        return result
                .stream()
                .filter(item -> !current
                        .getStorageTable()
                        .get(item)
                        .equals(previous
                                .getStorageTable()
                                .get(item)))
                .collect(Collectors.toSet());
    }
}
