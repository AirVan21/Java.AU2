package ru.spbau.db;

import com.mongodb.MongoClient;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import ru.spbau.db.entity.Branch;

import java.util.List;

/**
 *
 */
public class DataBase {
    private final MongoClient mongo = new MongoClient();
    private final Datastore datastore;

    public DataBase(String name) {
        datastore = new Morphia().createDatastore(mongo, name);
    }

    // TODO: remove
    public void init(String name) {
        List<Branch> query = datastore
                .find(Branch.class)
                .field("name")
                .equal(name)
                .asList();

        if (!query.isEmpty()) {
            return;
        }

        datastore.save(new Branch(name, true));
    }
}
