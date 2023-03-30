package wtf.casper.papertests.tests.storagetest;

import wtf.casper.amethyst.core.storage.Credentials;
import wtf.casper.amethyst.core.storage.impl.fstorage.MariaDBFStorage;
import wtf.casper.amethyst.core.storage.impl.fstorage.SQLFStorage;

import java.util.UUID;

public class TestMariaDBStorage extends MariaDBFStorage<UUID, TestObject> {


    public TestMariaDBStorage(Credentials credentials) {
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
