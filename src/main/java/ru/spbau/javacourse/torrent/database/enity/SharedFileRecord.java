package ru.spbau.javacourse.torrent.database.enity;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.util.ArrayList;
import java.util.List;

/**
 * SharedFileRecord is a meta-data file which describes single file (available for torrent download)
 */
@Entity("SharedFileRecord")
public class SharedFileRecord {
    @Id
    private ObjectId id;
    private String fileName = "";
    private long fileSize;
    private boolean isPublished = false;
    private int fileServerId;
    private List<Boolean> filledChunks = new ArrayList<>();

    public SharedFileRecord() {}

    public SharedFileRecord(String fileName, long fileSize, List<Boolean> filledChunks, boolean isPublished) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.filledChunks = filledChunks;
        this.isPublished = isPublished;
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

    public boolean isPublished() {
        return isPublished;
    }

    public void setPublished(boolean published) {
        isPublished = published;
    }

    public int getFileServerId() {
        return fileServerId;
    }

    public void setFileServerId(int fileServerId) {
        this.fileServerId = fileServerId;
    }

    public List<Boolean> getFilledChunks() {
        return filledChunks;
    }
}
