package ru.spbau.db.entity;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
@Entity("Commit")
public class Commit {
    @Id
    private ObjectId id;
    private String message;
    private Date date;
    private Branch branch;
    private final Map<String, ObjectId> storageTable = new HashMap<>();

    public Commit() {}

    public Commit(String message, Date date, Branch branch) {
        this.message = message;
        this.date = date;
        this.branch = branch;
    }

    public ObjectId getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public Date getDate() {
        return date;
    }

    public Branch getBranch() {
        return branch;
    }

    public Map<String, ObjectId> getStorageTable() {
        return storageTable;
    }

    public void addFile(String path, ObjectId fileId) {
        storageTable.put(path, fileId);
    }

    public void removeFile(String path) {
        storageTable.remove(path);
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || other.getClass() != getClass()) {
            return false;
        }

        return id.equals(((Commit) other).id);
    }

}
