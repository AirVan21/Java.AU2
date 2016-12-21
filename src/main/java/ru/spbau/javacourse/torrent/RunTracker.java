package ru.spbau.javacourse.torrent;

import ru.spbau.javacourse.torrent.tracker.Tracker;
import ru.spbau.javacourse.torrent.utils.GlobalConstants;

import java.io.IOException;

/**
 * Starts Torrent Tracker
 */
public class RunTracker {
    public static void main(String[] args) throws IOException {
        final Tracker tracker = new Tracker();
        tracker.start(GlobalConstants.TRACKER_PORT);
    }
}
