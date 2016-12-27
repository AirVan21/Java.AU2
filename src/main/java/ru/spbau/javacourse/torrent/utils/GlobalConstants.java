package ru.spbau.javacourse.torrent.utils;

/**
 * GlobalConstants class declares constants which are used in torrent project
 */
public class GlobalConstants {
    public final static long CHUNK_SIZE = 1024;
    public final static String DOWNLOAD_DIR = "src/test/downloads";
    public final static String SERVER_DB_DIR = "src/test/server";
    public final static String CLIENT_DB_NAME = "CLIENT_FILE_DB";
    public final static String DEFAULT_HOST = "127.0.0.1";
    public final static short TRACKER_PORT = 8081;
    public final static short CLIENT_PORT_FST = 8841;
    public final static short CLIENT_PORT_SND = 8842;
}
