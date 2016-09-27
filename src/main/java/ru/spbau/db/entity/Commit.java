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
    private String branch;

    public Commit() {}

    public Commit(String message, String author, Date date, String branch) {
        this.message = message;
        this.author = author;
        this.date = date;
        this.branch = branch;
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

    public String getBranch() {
        return branch;
    }
}
