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
        return current.storageTable
                .keySet()
                .stream()
                .filter(item -> !previous.storageTable.containsKey(item))
                .collect(Collectors.toSet());
    }

    public static Set<String> getDeletedFiles(Commit current, Commit previous) {
        return previous.storageTable
                .keySet()
                .stream()
                .filter(item -> !current.storageTable.containsKey(item))
                .collect(Collectors.toSet());
    }

    public static Set<String> getModifiedFiles(Commit current, Commit previous) {
        final Set<String> result = new HashSet<>(current.storageTable.keySet());
        result.retainAll(previous.storageTable.keySet());

        return result
                .stream()
                .filter(item -> !current
                        .storageTable
                        .get(item)
                        .equals(previous
                                .storageTable
                                .get(item)))
                .collect(Collectors.toSet());
    }
}
