package ru.spbau;

import ru.spbau.db.DataBase;

/**
 *
 */
public class VCS {
    private final DataBase db;
    private final static String DATABASE_NAME = "VCS";

    public VCS() {
        db = new DataBase(DATABASE_NAME);
    }


}
