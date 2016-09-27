package ru.spbau;

import ru.spbau.db.DataBase;
import ru.spbau.utility.StatusManager;

import java.util.List;

/**
 *
 */
public class VCS {
    private final DataBase db;
    private final static String DATABASE_NAME = "VCS";

    public VCS() {
        db = new DataBase(DATABASE_NAME);
        initialize();
    }

    public void initialize() {

    }

    public StatusManager getStatus() {
        return null;
    }

    List<String> getLog() {
        return null;
    }

    private static String getPath() {
        return System.getProperty("user.dir");
    }

}
