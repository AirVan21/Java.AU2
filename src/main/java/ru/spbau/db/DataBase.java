package ru.spbau.db;

import com.mongodb.MongoClient;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import ru.spbau.db.entity.Branch;
import ru.spbau.db.entity.Commit;
import ru.spbau.db.entity.File;

/**
 * Created by airvan21 on 27.09.16.
 */
public class DataBase {
    private final MongoClient mongo = new MongoClient();
    private final Datastore datastore;

    public DataBase(String name) {
        datastore = new Morphia().createDatastore(mongo, name);
        initialize();
    }

    private void initialize() {
        datastore.save(new File());
        datastore.save(new Commit());
        datastore.save(new Branch());
    }
}
