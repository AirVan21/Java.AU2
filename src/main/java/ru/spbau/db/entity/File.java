package ru.spbau.db.entity;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/**
 *
 */
@Entity("File")
public class File {
    @Id
    private ObjectId id;
    private Commit commit;
    private String text;
    private String path;

    public File() {}

    public File(Commit commit, String text, String path) {
        this.commit = commit;
        this.text = text;
        this.path = path;
    }

    public Commit getCommit() {
        return commit;
    }

    public String getText() {
        return text;
    }

    public String getPath() {
        return path;
    }
}
