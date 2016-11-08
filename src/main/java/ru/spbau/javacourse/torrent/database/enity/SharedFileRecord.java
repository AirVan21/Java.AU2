package ru.spbau.javacourse.torrent.database.enity;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/**
 * SharedFileRecord is a meta-data file which describes single file (available for torrent download)
 */
@Entity("SharedFileRecord")
public class SharedFileRecord {
    @Id
    private ObjectId id;
    private String fileName;
    private long fileSize;

    public SharedFileRecord() {}

    public SharedFileRecord(String fileName, long fileSize) {
        this.fileName = fileName;
        this.fileSize = fileSize;
    }

    public ObjectId getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public long getFileSize() {
        return fileSize;
    }
}
