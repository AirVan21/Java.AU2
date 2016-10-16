package ru.spbau.db.entity;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import ru.spbau.utility.FileManager;

import java.util.Optional;

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

    public File(String path) {
        this.path = path;
        text = extractText(path);
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

    private String extractText(String path) {
        String result = "";
        Optional<java.io.File> file = FileManager.getFile(path);
        if (file.isPresent()) {
            Optional<String> fileText = FileManager.readFile(file.get());
            result = fileText.isPresent() ? fileText.get() : result;
        }

        return result;
    }
}
