package ru.spbau.db.entity;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Commit class represents revision entity in VCS project
 */
@Entity("Commit")
public class Commit {
    @Id
    private ObjectId id;
    public String message;
    public Date date;
    public String branchName;
    public final Map<String, ObjectId> storageTable = new HashMap<>();

    public Commit() {}

    public Commit(String message, Date date, String branch) {
        this.message = message;
        this.date = date;
        this.branchName = branch;
    }

    public ObjectId getId() {
        return id;
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
