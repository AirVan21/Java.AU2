package ru.spbau.db.entity;

import com.mongodb.BasicDBObject;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.NotSaved;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Commit class represents revision entity in VCS project
 */
@Entity("Commit")
public class Commit {
    @Id
    private ObjectId id;
    public String message = "";
    public Date date;
    public String branchName = "";
    @NotSaved
    public Map<String, ObjectId> storageTable = new HashMap<>();
    public BasicDBObject mongoTable = new BasicDBObject();

    public Commit() {}

    public Commit(Map<String, ObjectId> storageTable) {
        this.storageTable = storageTable;
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
