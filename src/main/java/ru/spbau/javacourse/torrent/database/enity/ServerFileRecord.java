package ru.spbau.javacourse.torrent.database.enity;

import java.io.Serializable;

/**
 * File record which will be stored on a server
 */
public class ServerFileRecord implements Serializable {
    private String name;
    private long size;
    private String source;
    private int id;

    public ServerFileRecord(String name, long size, String source) {
        this.name = name;
        this.size = size;
        this.source = source;
        this.id = hashCode();
    }

    public String getName() {
        return name;
    }

    public long getSize() {
        return size;
    }

    public String getSource() {
        return source;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ServerFileRecord record = (ServerFileRecord) o;

        if (size != record.size) return false;
        if (!name.equals(record.name)) return false;
        return source.equals(record.source);

    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (int) (size ^ (size >>> 32));
        result = 31 * result + source.hashCode();
        return result;
    }
}
