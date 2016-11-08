package ru.spbau.javacourse.torrent.database;

import com.mongodb.MongoClient;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

/**
 * DataBase class represents a MongoDB wrapper for torrent project
 */
public class DataBase {
    private final Datastore datastore;

    public DataBase(String name) {
        final MongoClient mongo = new MongoClient();
        datastore = new Morphia().createDatastore(mongo, name);
    }
}
