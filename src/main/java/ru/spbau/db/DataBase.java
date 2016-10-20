package ru.spbau.db;

import com.mongodb.MongoClient;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.UpdateOperations;
import ru.spbau.db.entity.Branch;
import ru.spbau.db.entity.Commit;
import ru.spbau.db.entity.File;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * DataBase class represents a MongoDB wrapper for VCS project
 */
public class DataBase {
    private final Datastore datastore;
    private final Logger logger = Logger.getLogger(DataBase.class.getName());

    public DataBase(String name) {
        final MongoClient mongo = new MongoClient();
        datastore = new Morphia().createDatastore(mongo, name);
    }

    public Branch createBranch(String name, boolean isActive) {
        final Branch branch = new Branch(name, isActive);
        datastore.save(branch);

        return branch;
    }

    public Optional<Branch> getActiveBranch() {
        final List<Branch> query = datastore
                .find(Branch.class)
                .field("isActive")
                .equal(true)
                .asList();

        if (query.isEmpty() || query.size() > 1) {
            logger.info("Couldn't find active branch!");
            return Optional.empty();
        }

        return Optional.of(query.get(0));
    }

    public void deactivateBranch() {
        Optional<Branch> currentBranch = getActiveBranch();
        if (!currentBranch.isPresent()) {
            return;
        }

        currentBranch.get().setActive(false);
        UpdateOperations<Branch> update = datastore
                .createUpdateOperations(Branch.class)
                .set("isActive", false);
        datastore.update(currentBranch.get(), update);
    }

    public Optional<Branch> getBranch(String name) {
        final List<Branch> query = datastore
                .find(Branch.class)
                .field("name")
                .equal(name)
                .asList();

        if (query.isEmpty() || query.size() > 1) {
            logger.info("Couldn't find appropriate branch!");
            return Optional.empty();
        }

        return Optional.of(query.get(0));
    }

    public Set<Branch> getBranches() {
        return datastore
                .find(Branch.class)
                .field("isClosed")
                .equal(false)
                .asList()
                .stream()
                .collect(Collectors.toSet());
    }

    public void closeBranch(String name) {
        Optional<Branch> branch = getBranch(name);
        if (!branch.isPresent()) {
            return;
        }

        UpdateOperations<Branch> update = datastore
                .createUpdateOperations(Branch.class)
                .set("isClosed", true);
        datastore.update(branch.get(), update);
    }

    public void makeCommit(String message, String branchName, Commit commit) {
        final Date currentDate = Calendar.getInstance().getTime();
        commit.message = message;
        commit.date = currentDate;
        commit.branchName = branchName;
        restoreDataToMongoHash(commit);
        // Save commit
        datastore.save(commit);
        updateInfoInFiles(commit);
    }

    public void saveCommit(Commit commit) {
        restoreDataToMongoHash(commit);
        // Save commit
        datastore.save(commit);
    }

    public Optional<Commit> getCommit(ObjectId id) {
        final List<Commit> query = datastore
                .find(Commit.class)
                .field("id")
                .equal(id)
                .asList()
                .stream()
                .peek(Commit::updateStorageTable)
                .collect(Collectors.toList());

        if (query.isEmpty() || query.size() > 1) {
            logger.info("Couldn't find appropriate branch!");
            return Optional.empty();
        }

        return Optional.of(query.get(0));
    }

    public List<Commit> getCommits(String branchName) {
        return datastore
                .find(Commit.class)
                .field("branchName")
                .equal(branchName)
                .asList();
    }

    public List<Commit> getLog(String branchName) {
        return datastore
                .find(Commit.class)
                .field("branchName")
                .equal(branchName)
                .asList()
                .stream()
                .peek(Commit::updateStorageTable)
                .collect(Collectors.toList());
    }

    public List<File> getCommittedFiles(Commit commit) {
        return datastore
                .find(File.class)
                .field("commitId")
                .equal(commit.getId())
                .asList();
    }

    /**
     * Gets last committed revision
     *
     * @return Optional of last committed revision if such revision exists
     */
    public Optional<Commit> getLastCommittedRevision(String branchName) {
        List<Commit> revisions = datastore
                .find(Commit.class)
                .field("branchName")
                .equal(branchName)
                .order("date")
                .asList();

        if (revisions.isEmpty()) {
            return Optional.empty();
        }
        Commit result = revisions.get(revisions.size() - 1);
        result.updateStorageTable();

        return Optional.of(result);
    }

    public ObjectId addFile(File file) {
        return (ObjectId) datastore.save(file).getId();
    }

    public List<File> getFile(ObjectId id) {
        return datastore
                .find(File.class)
                .field("id")
                .equal(id)
                .asList();
    }

    public void dropDatabase() {
        datastore.getDB().dropDatabase();
    }

    private void restoreDataToMongoHash(Commit commit) {
        final List<String> paths = new ArrayList<>(commit.storageTable.keySet());
        final List<ObjectId> ids = new ArrayList<>(commit.storageTable.values());
        commit.mongoTable.clear();
        for (int i = 0; i < paths.size(); ++i) {
            commit.mongoTable.put(ids.get(i).toString(), paths.get(i));
        }
    }

    private void updateInfoInFiles(Commit commit) {
        final UpdateOperations<File> update = datastore
                .createUpdateOperations(File.class)
                .set("commitId", commit.getId());
        commit.storageTable
                .values()
                .forEach(fileId -> {
                    List<File> file = getFile(fileId);
                    if (!file.isEmpty()) {
                        datastore.update(file.get(0), update);
                    }
                });
    }
}
