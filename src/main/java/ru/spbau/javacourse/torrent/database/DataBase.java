package ru.spbau.javacourse.torrent.database;

import com.mongodb.MongoClient;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import ru.spbau.javacourse.torrent.database.enity.SharedFileRecord;

import java.util.List;

/**
 * DataBase class represents a MongoDB wrapper for torrent project
 */
public class DataBase {
    private final Datastore datastore;

    public DataBase(String name) {
        final MongoClient mongo = new MongoClient();
        datastore = new Morphia().createDatastore(mongo, name);
    }

    public void saveSharedFileRecord(SharedFileRecord record) {
        datastore.save(record);
    }

    public List<SharedFileRecord> getPublishedFiles() {
        return datastore
                .find(SharedFileRecord.class)
                .field("isPublished")
                .equal(true)
                .asList();
    }
}
