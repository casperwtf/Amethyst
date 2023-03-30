package wtf.casper.papertests.tests.storagetest;

import wtf.casper.amethyst.core.storage.Credentials;
import wtf.casper.amethyst.core.storage.impl.fstorage.SQLFStorage;
import wtf.casper.amethyst.core.storage.impl.fstorage.SQLiteFStorage;

import java.io.File;
import java.util.UUID;

public class TestSQLStorage extends SQLFStorage<UUID, TestObject> {


    public TestSQLStorage(Credentials credentials) {
        super(UUID.class, TestObject.class, credentials.getTable("test"), credentials);
    }

    @Override
    public TestObject constructValue(UUID key) {
        return new TestObject(key);
    }

    @Override
    public TestObject constructValue() {
        return new TestObject(null);
    }
}
