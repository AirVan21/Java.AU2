package ru.spbau.javacourse.torrent.utils;

/**
 * GlobalConstants class declares constants which are used in torrent project
 */
public class GlobalConstants {
    public final static long CHUNK_SIZE = 1024 * 1024;
    public final static String DOWNLOAD_DIR = "/downloads";
    public final static String CLIENT_DB_NAME = "CLIENT_FILE_DB";
    public final static String DEFAULT_HOST = "localhost";
    public final static int TRACKER_PORT = 8840;
    public final static int CLIENT_PORT_FST = 8841;
    public final static int CLIENT_PORT_SND = 8842;
}
