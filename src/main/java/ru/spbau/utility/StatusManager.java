package ru.spbau.utility;

import java.util.Set;

/**
 *
 */
public class StatusManager {
    private final Set<String> addedFiles;
    private final Set<String> deletedFiles;
    private final Set<String> modifiedFiles;

    public StatusManager(Set<String> addedFiles, Set<String> deletedFiles, Set<String> modifiedFiles) {
        this.addedFiles = addedFiles;
        this.deletedFiles = deletedFiles;
        this.modifiedFiles = modifiedFiles;
    }

    public Set<String> getAddedFiles() {
        return addedFiles;
    }

    public Set<String> getDeletedFiles() {
        return deletedFiles;
    }

    public Set<String> getModifiedFiles() {
        return modifiedFiles;
    }
}
