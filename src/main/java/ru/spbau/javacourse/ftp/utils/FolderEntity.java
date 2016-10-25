package ru.spbau.javacourse.ftp.utils;

public class FolderEntity {
    private final String name;
    private final boolean isFolder;

    public FolderEntity(String name, boolean isFolder) {
        this.name = name;
        this.isFolder = isFolder;
    }

    public String getName() {
        return name;
    }

    public boolean isFolder() {
        return isFolder;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof FolderEntity)) {
            return false;
        }

        final FolderEntity test = (FolderEntity) other;
        return isFolder() == test.isFolder() && getName().equals(test.getName());
    }
}
