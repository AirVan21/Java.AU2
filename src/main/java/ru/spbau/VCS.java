package ru.spbau;

import ru.spbau.db.DataBase;
import ru.spbau.db.entity.Branch;
import ru.spbau.db.entity.Commit;
import ru.spbau.utility.StatusManager;

import java.util.List;

/**
 *
 */
public class VCS {
    private final static String DATABASE_NAME = "VCS";
    private final DataBase database = new DataBase(DATABASE_NAME);
    private final String pathToWorkingFolder;
    private Branch branch = new Branch();
    private Commit revision = new Commit();

    public VCS(String pathToWorkingDir) {
        pathToWorkingFolder = pathToWorkingDir;
    }

    public String getStatus() {
        return null;
    }

    List<String> getLog() {
        return null;
    }

    public void makeCommit(String author, String message) {

    }

    public void makeInit() {

    }
}
