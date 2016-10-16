package ru.spbau;

import ru.spbau.db.DataBase;
import ru.spbau.db.entity.Branch;
import ru.spbau.db.entity.Commit;
import ru.spbau.utility.GlobalLogger;
import ru.spbau.utility.StatusManager;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 *
 */
public class VCS {
    private final static String DATABASE_NAME = "VCS";
    private final static String MASTER_NAME = "master";
    private final DataBase database = new DataBase(DATABASE_NAME);
    private final Path pathToWorkDir;
    private Branch branch = new Branch();
    private Commit revision = new Commit();

    public VCS(Path pathToWorkingDir) {
        pathToWorkDir = pathToWorkingDir;
    }

    public String getStatus() {
        final Optional<Commit> lastCommittedRevision = database.getLastCommittedRevision();
        final Commit previousRevision = lastCommittedRevision.isPresent()
                ? lastCommittedRevision.get()
                : new Commit();

        final Set<String> addedFiles = StatusManager.getAddedFiles(revision, previousRevision);
        final Set<String> deletedFiles = StatusManager.getDeletedFiles(revision, previousRevision);
        final Set<String> modifiedFiles = StatusManager.getModifiedFiles(revision, previousRevision);

        final StringBuilder sb = new StringBuilder();
        addedFiles.forEach(name -> sb.append("new file: ").append(name).append("\n"));
        deletedFiles.forEach(name -> sb.append("modified: ").append(name).append("\n"));
        modifiedFiles.forEach(name -> sb.append("deleted: ").append(name).append("\n"));

        return sb.toString();
    }

    String getLog() {
        List<Commit> commits = database.getLog(branch);
        final StringBuilder sb = new StringBuilder();
        commits.forEach(commit -> sb.append(commit).append("\n"));

        return sb.toString();
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
