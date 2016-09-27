package ru.spbau.db.entity;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/**
 *
 */
@Entity("Branch")
public class Branch {
    @Id
    private ObjectId id;
    private String name;
    private boolean isActive = false;
    private boolean isClosed = false;

    public Branch() {}

    public Branch(String branchName, boolean active) {
        name = branchName;
        isActive = active;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void setClosed(boolean closed) {
        isClosed = closed;
    }
}
