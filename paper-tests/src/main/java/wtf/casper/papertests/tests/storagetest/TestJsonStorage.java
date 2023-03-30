package wtf.casper.papertests.tests.storagetest;

import wtf.casper.amethyst.core.storage.impl.fstorage.JsonFStorage;

import java.io.File;
import java.util.UUID;

public class TestJsonStorage extends JsonFStorage<UUID, TestObject> {

    public TestJsonStorage(File file) {
        super(file, TestObject.class);
    }

    @Override
    public TestObject constructValue(UUID key) {
        return new TestObject(key);
    }
}
