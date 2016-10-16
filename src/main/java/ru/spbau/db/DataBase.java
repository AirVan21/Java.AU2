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

    /**
     * TODO: rewrite
     * @param files
     * @param message
     * @param author
     * @param branch
     */
    public void makeCommit(Map<String, String> files, String message, String author, Branch branch) {
        final Date currentDate = Calendar.getInstance().getTime();
        final Commit commit = new Commit(message, author, currentDate, branch);

        // Save commit
        datastore.save(commit);

        // Save files
        files.entrySet()
                .stream()
                .map(item -> new File(item.getKey(), item.getValue()))
                .forEach(datastore::save);
    }

    public Optional<Commit> getCommit(ObjectId id) {
        final List<Commit> query = datastore
                .find(Commit.class)
                .field("id")
                .equal(id)
                .asList();

        if (query.isEmpty() || query.size() > 1) {
            logger.info("Couldn't find appropriate branch!");
            return Optional.empty();
        }

        return Optional.of(query.get(0));
    }

    public List<Commit> getLog(Branch branch) {
        return datastore
                .find(Commit.class)
                .field("branchName")
                .equal(branch.getName())
                .asList();
    }

    /**
     * @param commit
     * @return
     */
    public List<File> getCommitedFiles(Commit commit) {
        return datastore
                .find(File.class)
                .field("commit")
                .equal(commit)
                .asList();
    }

    /**
     * Gets last committed revison
     * @return
     */
    public Optional<Commit> getLastCommittedRevision() {
        List<Commit> revisions = datastore
                .find(Commit.class)
                .order("date")
                .asList();

        return revisions.isEmpty()
                ? Optional.empty()
                : Optional.of(revisions.get(revisions.size() - 1));
    }


    public void dropDatabase() {
        datastore.getDB().dropDatabase();
    }
}
