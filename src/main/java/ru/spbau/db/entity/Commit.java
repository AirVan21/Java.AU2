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

    public void updateStorageTable() {
        if (storageTable.isEmpty()) {
            for (Map.Entry<String, Object> item : mongoTable.entrySet()) {
                ObjectId value = new ObjectId(item.getKey());
                String key = (String) item.getValue();
                storageTable.put(key, value);
            }
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || other.getClass() != getClass()) {
            return false;
        }

        return id.equals(((Commit) other).id);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Commit : ").append(id).append("\n");
        sb.append("Date   : ").append(date).append("\n");
        sb.append("Message: ").append(message).append("\n");

        return sb.toString();
    }

}
