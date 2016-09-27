package ru.spbau.db.entity;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.util.Date;

/**
 *
 */
@Entity("Commit")
public class Commit {
    @Id
    private ObjectId id;
    private String message;
    private String author;
    private Date date;
    private String branchName;

    public Commit() {}

    public Commit(String message, String author, Date date, String branchName) {
        this.message = message;
        this.author = author;
        this.date = date;
        this.branchName = branchName;
    }

    public ObjectId getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public String getAuthor() {
        return author;
    }

    public Date getDate() {
        return date;
    }

    public String getBranchName() {
        return branchName;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || other.getClass() != getClass()) {
            return false;
        }

        return id.equals(((Commit) other).id);
    }
}
