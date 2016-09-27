package ru.spbau;

import ru.spbau.db.DataBase;

/**
 * Created by airvan21 on 27.09.16.
 */
public class VCS {
    private final DataBase db;
    private final static String DATABASE_NAME = "VCS";

    public VCS() {
        db = new DataBase();
    }
}
