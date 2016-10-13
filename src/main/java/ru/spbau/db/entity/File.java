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
    private String text;
    private String path;
    private Commit commit = new Commit();

    public File() {}

    public File(String text, String path) {
        this.text = text;
        this.path = path;
    }

    public ObjectId getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getPath() {
        return path;
    }

    public Commit getCommit() {
        return commit;
    }

    public void setCommit(Commit commit) {
        this.commit = commit;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || other.getClass() != getClass()) {
            return false;
        }

        return id.equals(((File) other).id);
    }
}
