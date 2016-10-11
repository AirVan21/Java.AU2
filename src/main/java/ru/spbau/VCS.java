package ru.spbau;

import ru.spbau.db.DataBase;
import ru.spbau.utility.StatusManager;

import java.util.List;

/**
 *
 */
public class VCS {
    private final static String DATABASE_NAME = "VCS";
    private final DataBase database = new DataBase(DATABASE_NAME);
    private final String pathToWorkingFolder;

    public VCS(String pathToWorkingDir) {
        pathToWorkingFolder = pathToWorkingDir;
    }

    public StatusManager getStatus() {
        return null;
    }

    List<String> getLog() {
        return null;
    }

    public void makeCommit(String author, String message) {
    }
}
