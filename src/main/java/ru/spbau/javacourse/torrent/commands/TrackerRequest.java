package ru.spbau.javacourse.torrent.commands;

/**
 * TrackerRequest class represents supported commands to Tracker
 */
public class TrackerRequest {
    public final static byte GET_LIST_REQUEST = 1;
    public final static byte GET_UPLOAD_REQUEST = 2;
    public final static byte GET_SOURCES_REQUEST = 3;
    public final static byte GET_UPDATE_REQUEST = 4;
}
