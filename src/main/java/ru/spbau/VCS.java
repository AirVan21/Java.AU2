package ru.spbau;

import org.bson.types.ObjectId;
import ru.spbau.db.DataBase;
import ru.spbau.db.entity.Branch;
import ru.spbau.db.entity.Commit;
import ru.spbau.db.entity.File;
import ru.spbau.utility.FileManager;
import ru.spbau.utility.GlobalLogger;
import ru.spbau.utility.StatusManager;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

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

    public Branch getBranch() {
        return branch;
    }

    public Commit getRevision() {
        return revision;
    }

    public void dropVCSInfo() {
        database.dropDatabase();
    }

    public boolean isValid() {
        return !branch.isEmpty();
    }

    public void makeInit() {
        if (branch.isEmpty()) {
            final boolean active = true;
            branch = database.createBranch(MASTER_NAME, active);
        } else {
            GlobalLogger.log("Repository is already initiated!");
        }
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

    String getBranches() {
        List<Branch> branches = database.getBranches();
        final StringBuilder sb = new StringBuilder();
        branches.forEach(item -> sb.append(item.getName()).append("\n"));

        return sb.toString();
    }

    public void makeCommit(String message) {
        final Date currentDate = Calendar.getInstance().getTime();
        revision.message = message;
        revision.date = currentDate;
        revision.branchName = branch.getName();

        database.makeCommit(revision);
    }

    public void makeAdd(List<String> files) {
        final boolean isRecursive = true;
        final Set<String> availableFiles = FileManager.listFiles(pathToWorkDir.toString(), isRecursive);
        final List<File> addedFiles = files.stream()
                .filter(availableFiles::contains)
                .map(File::new)
                .collect(Collectors.toList());
        addedFiles.forEach(file -> {
            ObjectId id = database.addFile(file);
            revision.addFile(file.getPath(), id);
        });
    }

    public void makeClean() {
        final boolean isRecursive = true;
        final Set<String> availableFiles = FileManager.listFiles(pathToWorkDir.toString(), isRecursive);
        availableFiles
                .stream()
                .filter(item -> !revision.storageTable.containsKey(item))
                .forEach(FileManager::deleteFile);
    }

    public void makeRm(List<String> files) {
        final boolean isRecursive = true;
        final Set<String> availableFiles = FileManager.listFiles(pathToWorkDir.toString(), isRecursive);
        files.stream()
                .filter(availableFiles::contains)
                .forEach(file -> {
                    revision.removeFile(file);
                    FileManager.deleteFile(file);
                });
    }

    public void makeCheckout(String branchName) {
        
    }
}
