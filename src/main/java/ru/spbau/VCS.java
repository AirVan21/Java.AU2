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
        revision = new Commit();
    }

    public boolean isValid() {
        return !branch.isEmpty();
    }

    public void makeInit() {
        if (branch.isEmpty()) {
            final boolean active = true;
            branch = database.createBranch(MASTER_NAME, active);
            database.makeCommit("Initialization commit", branch.getName(), revision);
            revision = new Commit(revision.storageTable);
        } else {
            GlobalLogger.log("Repository is already initiated!");
        }
    }

    public String getStatus() {
        final Optional<Commit> lastCommittedRevision = database.getLastCommittedRevision(branch.getName());
        final Commit previousRevision = lastCommittedRevision.isPresent()
                ? lastCommittedRevision.get()
                : new Commit();

        final Set<String> addedFiles = StatusManager.getAddedFiles(revision, previousRevision);
        final Set<String> deletedFiles = StatusManager.getDeletedFiles(revision, previousRevision);
        final Set<String> modifiedFiles = StatusManager.getModifiedFiles(revision, previousRevision);

        final StringBuilder sb = new StringBuilder();
        addedFiles.forEach(name -> sb.append("new file: ").append(name).append("\n"));
        deletedFiles.forEach(name -> sb.append("deleted: ").append(name).append("\n"));
        modifiedFiles.forEach(name -> sb.append("modified: ").append(name).append("\n"));

        return sb.toString();
    }

    public String getLog() {
        List<Commit> commits = database.getLog(branch.getName());
        final StringBuilder sb = new StringBuilder();
        commits.forEach(commit -> sb.append(commit).append("\n"));

        return sb.toString();
    }

    public String getBranches() {
        Set<Branch> branches = database.getBranches();
        final StringBuilder sb = new StringBuilder();
        branches.forEach(item -> sb.append(item.getName()).append("\n"));

        return sb.toString();
    }

    public void makeCommit(String message) {
        database.makeCommit(message, branch.getName(), revision);
        revision = new Commit(revision.storageTable);
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

    public String makeCheckout(String branchName) {
        final Optional<Commit> lastCommittedRevision = database.getLastCommittedRevision(branch.getName());
        final Commit previousRevision = lastCommittedRevision.isPresent()
                ? lastCommittedRevision.get()
                : new Commit();
        final Set<String> addedFiles = StatusManager.getAddedFiles(revision, previousRevision);
        final Set<String> deletedFiles = StatusManager.getDeletedFiles(revision, previousRevision);
        final Set<String> modifiedFiles = StatusManager.getModifiedFiles(revision, previousRevision);
        if (!deletedFiles.isEmpty() || !addedFiles.isEmpty() || !modifiedFiles.isEmpty()) {
            return "Please, commit your changes before checkout!";
        }
        final Optional<Branch> nextBranch = database.getBranch(branchName);
        if (!nextBranch.isPresent()) {
            return "Specified branch \"" + branchName + "\" is not found!";
        }

        final Optional<Commit> nextBranchRevision = database.getLastCommittedRevision(nextBranch.get().getName());
        if (!nextBranchRevision.isPresent()) {
            return "Database error occurred!";
        }

        return "Switched to branch '" + branchName + "'";
    }

    public void makeReset(List<String> arguments) {
        for (final String fileName : arguments) {
            if (revision.storageTable.containsKey(fileName)) {
                Optional<Commit> previousRevision = database.getLastCommittedRevision(branch.getName());
                if (previousRevision.isPresent()) {
                    // Gets previous version of the file
                    Set<File> committedFiles = new HashSet<>(database.getCommittedFiles(previousRevision.get()));
                    Optional<File> resultFile = committedFiles
                            .stream()
                            .filter(item -> item.getPath().equals(fileName))
                            .findAny();
                    // Rewrites file
                    if (resultFile.isPresent()) {
                        FileManager.updateFile(resultFile.get());
                    } else {
                        FileManager.deleteFile(fileName);
                    }
                }
            }
        }
    }

    public void makeMerge(String branchName) {
        if (branchName.equals(branch.getName())) {
            return;
        }
    }

    public void makeBranch(String branchName) {
        final Set<Branch> existingBranches = database.getBranches();
        final Set<String> branchNames = existingBranches
                .stream()
                .map(Branch::getName)
                .collect(Collectors.toSet());
        if (branchNames.contains(branchName)) {
            return;
        }

        database.deactivateBranch();
        final boolean isActive = true;
        final List<Commit> branchCommits = database.getCommits(branch.getName());
        branch = database.createBranch(branchName, isActive);
        branchCommits
                .forEach(item -> {
                    item.branchName = branch.getName();
                    database.saveCommit(item);
                });
    }
}
