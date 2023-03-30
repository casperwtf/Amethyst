package wtf.casper.papertests.tests;

import wtf.casper.amethyst.core.obj.Pair;
import wtf.casper.amethyst.core.storage.Credentials;
import wtf.casper.amethyst.core.storage.FieldStorage;
import wtf.casper.amethyst.core.storage.StatelessFieldStorage;
import wtf.casper.amethyst.core.storage.StorageType;
import wtf.casper.papertests.PaperAmethystTests;
import wtf.casper.papertests.Test;
import wtf.casper.papertests.tests.storagetest.*;

import java.io.File;
import java.util.Collection;
import java.util.UUID;

public class StorageTest implements Test {

    private final PaperAmethystTests plugin;
    private FieldStorage<UUID, TestObject> storage;

    public StorageTest() {
        this.plugin = PaperAmethystTests.getPlugin(PaperAmethystTests.class);
    }

    @Override
    public boolean test() {
        try {
            createConnection(plugin);

            for (TestObject testObject : storage.allValues().join()) {
                storage.remove(testObject).join();
            }

            UUID key = UUID.randomUUID();

            assertVal(storage.get(key).join(), null);

            TestObject object = storage.getOrDefault(key).join();
            assertVal(object.getName(), "John");

            object.setName("Jane");
            storage.save(object).join();

            Collection<TestObject> objects1 = storage.get(StatelessFieldStorage.FilterType.EQUALS, StatelessFieldStorage.SortingType.NONE, Pair.of("name", "Jane")).join();
            assertVal(objects1.size(), 1);
            assertVal(objects1.iterator().next().getUniqueId(), key);

            Collection<TestObject> objects = storage.get(Pair.of("name", "Jane")).join();
            assertVal(objects.size(), 1);
            assertVal(objects.iterator().next().getUniqueId(), key);

            TestObject join1 = storage.getFirst("name", "Jane").join();
            if (join1 == null) {
                throw new AssertionError("join1 is null");
            }
            assertVal(join1.getUniqueId(), key);

            Collection<TestObject> join2 = storage.get("name", "Jane").join();
            assertVal(join2.size(), 1);
            assertVal(join2.iterator().next().getUniqueId(), key);

            Collection<TestObject> testObjects = storage.allValues().join();
            assertVal(testObjects.size(), 1);

            Boolean join = storage.contains("name", "Jane").join();
            assertVal(join, true);

            storage.remove(object).join();
            assertVal(storage.contains("name", "Jane").join(), false);

            storage.save(new TestObject(key)).join();
            assertVal(storage.contains("name", "Jane").join(), false);

            storage.write().join();
            storage.close().join();

            createConnection(plugin);

            assertVal(storage.contains("name", "Jane").join(), false);
            assertVal(storage.contains("name", "John").join(), true);

            for (TestObject testObject : storage.allValues().join()) {
                storage.remove(testObject).join();
            }

            assertVal(storage.contains("name", "John").join(), false);

            storage.write().join();

            assertVal(storage.allValues().join().size(), 0);
            storage.close().join();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void createConnection(PaperAmethystTests plugin) {
        if (storage != null) {
            storage = null;
        }
        Credentials credentials = Credentials.from(plugin.getYamlConfig(), "storage-test");
        switch (credentials.getType(StorageType.JSON)) {
            case SQLITE:
                storage = new TestSQLiteStorage(new File(plugin.getDataFolder(), "testdb.db"), credentials);
                break;
            case MARIADB:
                storage = new TestMariaDBStorage(credentials);
                break;
            case MONGODB:
                storage = new TestMongoDBStorage(credentials);
                break;
            case JSON:
                storage = new TestJsonStorage(new File(plugin.getDataFolder(), "testdb.json"));
                break;
            case SQL:
                storage = new TestSQLStorage(credentials);
                break;
        }
    }
}
