package ru.spbau.javacourse.torrent.database;

import com.mongodb.MongoClient;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.UpdateOperations;
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

    public <T> List<ClientFileRecord> getFileRecords(String fieldName, T value) {
        return datastore
                .find(ClientFileRecord.class)
                .field(fieldName)
                .equal(value)
                .asList();
    }

    public <T> void updateFileRecord(ClientFileRecord record, String fieldName, T value) {
        UpdateOperations<ClientFileRecord> update = datastore
                .createUpdateOperations(ClientFileRecord.class)
                .set(fieldName, value);
        datastore.update(record, update);
    }

    public List<ClientFileRecord> getPublishedSharedFiles() {
        return datastore
                .find(ClientFileRecord.class)
                .field("isPublished")
                .equal(true)
                .asList();
    }

    public void dropDatabase() {
        datastore.getDB().dropDatabase();
    }

    public void dropCollection(Class source) {
        datastore.getCollection(source).drop();
    }
}
