package ru.spbau;

import ru.spbau.db.DataBase;
import ru.spbau.db.entity.Branch;
import ru.spbau.db.entity.Commit;
import ru.spbau.utility.GlobalLogger;
import ru.spbau.utility.StatusManager;

import java.util.Optional;
import java.util.Set;

/**
 *
 */
public class VCS {
    private final static String DATABASE_NAME = "VCS";
    private final static String MASTER_NAME = "master";
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

    String getLog() {
        final Optional<Commit> lastCommittedRevision = database.getLastCommittedRevision();
        final Commit previousRevision = lastCommittedRevision.isPresent()
                                ? lastCommittedRevision.get()
                                : new Commit();

        final Set<String> addedFiles = StatusManager.getAddedFiles(revision, previousRevision);
        final Set<String> deletedFiles = StatusManager.getDeletedFiles(revision, previousRevision);
        final Set<String> modifiedFiels = StatusManager.getModifiedFiles(revision, previousRevision);

        return "";
    }

    public void makeCommit(String author, String message) {

    }

    public void makeInit() {
        if (branch.isEmpty()) {
            final boolean active = true;
            branch = database.createBranch(MASTER_NAME, active);
        } else {
            GlobalLogger.log("Repository is already initiated!");
        }
    }
}
