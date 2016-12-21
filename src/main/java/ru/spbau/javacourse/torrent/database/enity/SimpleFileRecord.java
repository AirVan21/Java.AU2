package ru.spbau.javacourse.torrent.database.enity;


public class SimpleFileRecord {
    private final int id;
    private final String name;
    private final long size;

    public SimpleFileRecord(int id, String name, long size) {
        this.id = id;
        this.name = name;
        this.size = size;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getSize() {
        return size;
    }
}
