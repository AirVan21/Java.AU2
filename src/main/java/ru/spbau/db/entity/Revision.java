package ru.spbau.db.entity;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
@Entity
public class Revision {
    @Id
    ObjectId revisionId;
    Map<String, ObjectId> storageTable = new HashMap<>();

    public Revision() {}

    public Revision(Map<String, ObjectId> table) {
        storageTable = table;
    }

    public void addFile(String path, ObjectId fileId) {
        storageTable.put(path, fileId);
    }

    public void removeFile(String path) {
        storageTable.remove(path);
    }
}
