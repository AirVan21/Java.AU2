package ru.spbau.javacourse.torrent;

import ru.spbau.javacourse.torrent.tracker.Tracker;
import ru.spbau.javacourse.torrent.utils.GlobalConstants;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Tracker tracker = new Tracker();
        tracker.start(GlobalConstants.TRACKER_PORT);
    }
}
