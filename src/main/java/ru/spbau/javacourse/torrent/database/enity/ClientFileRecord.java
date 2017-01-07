package ru.spbau.javacourse.torrent.database.enity;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.util.ArrayList;
import java.util.List;

/**
 * ClientFileRecord is a meta-data file which describes single file (available for torrent download)
 */
@Entity("ClientFileRecord")
public class ClientFileRecord {
    @Id
    private ObjectId id;
    private String fileName = "";
    private String filePath = "";
    private long fileSize;
    private boolean isPublished = false;
    private int fileServerId;
    private List<Integer> availableChunks = new ArrayList<>();

    /**
     * Empty Constructor for Mongo DB
     */
    public ClientFileRecord() {}

    /**
     * File Record constructor
     * @param fileName - name of the file
     * @param fileSize - size of the file
     * @param availableChunks - ids of chunks which are empty
     * @param isPublished - flag, true is file is available for downloading (false otherwise)
     */
    public ClientFileRecord(String fileName, String filePath, long fileSize, List<Integer> availableChunks, boolean isPublished, int fileServerId) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.availableChunks = availableChunks;
        this.isPublished = isPublished;
        this.fileServerId = fileServerId;
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

    public List<Integer> getAvailableChunks() {
        return availableChunks;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
