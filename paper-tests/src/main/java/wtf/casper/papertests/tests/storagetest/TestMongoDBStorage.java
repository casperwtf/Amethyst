package wtf.casper.papertests.tests.storagetest;

import wtf.casper.amethyst.core.storage.Credentials;
import wtf.casper.amethyst.core.storage.impl.fstorage.MongoFStorage;

import java.util.UUID;

public class TestMongoDBStorage extends MongoFStorage<UUID, TestObject> {


    public TestMongoDBStorage(Credentials credentials) {
        super(TestObject.class, credentials);
    }

    @Override
    public TestObject constructValue(UUID key) {
        return new TestObject(key);
    }
}
