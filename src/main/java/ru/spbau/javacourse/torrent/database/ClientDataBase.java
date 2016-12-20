package ru.spbau.javacourse.torrent.database;

import com.mongodb.MongoClient;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import ru.spbau.javacourse.torrent.database.enity.ClientFileRecord;

import java.util.List;

/**
 * ClientDataBase class represents a MongoDB wrapper for torrent project
 */
public class ClientDataBase {
    private final Datastore datastore;

    public ClientDataBase(String name) {
        final MongoClient mongo = new MongoClient();
        datastore = new Morphia().createDatastore(mongo, name);
    }

    public void saveFileRecord(ClientFileRecord record) {
        datastore.save(record);
    }

    public List<ClientFileRecord> getPublishedSharedFiles() {
        return datastore
                .find(ClientFileRecord.class)
                .field("isPublished")
                .equal(true)
                .asList();
    }
}
