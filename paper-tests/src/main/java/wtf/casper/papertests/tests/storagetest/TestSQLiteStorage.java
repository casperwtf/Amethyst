package wtf.casper.papertests.tests.storagetest;

import wtf.casper.amethyst.core.storage.Credentials;
import wtf.casper.amethyst.core.storage.impl.fstorage.SQLiteFStorage;

import java.io.File;
import java.util.UUID;

public class TestSQLiteStorage extends SQLiteFStorage<UUID, TestObject> {

    public TestSQLiteStorage(File file, Credentials credentials) {
        super(UUID.class, TestObject.class, file, credentials.getTable("test"));
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
