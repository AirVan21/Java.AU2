package ru.spbau.db.entity;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/**
 * Branch class represents branch in VCS logic
 */
@Entity("Branch")
public class Branch {
    @Id
    private ObjectId id;
    /**
     * Branch name
     */
    private String name = "";
    /**
     * Shows is branch is active at the moment
     */
    private boolean isActive = false;
    /**
     * Shows is branch is closed or not
     */
    private boolean isClosed = false;

    public Branch() {}

    public Branch(String branchName, boolean active) {
        name = branchName;
        isActive = active;
    }

    public ObjectId getId() {
        return id;
    }

    public String getName() {
        return name;
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

    public boolean isEmpty() {
        return name.isEmpty();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || other.getClass() != getClass()) {
            return false;
        }

        return id.equals(((Branch) other).id);
    }
}
