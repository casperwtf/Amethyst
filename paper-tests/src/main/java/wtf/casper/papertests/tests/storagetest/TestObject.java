package wtf.casper.papertests.tests.storagetest;

import lombok.ToString;
import wtf.casper.amethyst.core.storage.id.Id;
import wtf.casper.amethyst.core.storage.id.StorageSerialized;

import java.util.UUID;

@ToString
public class TestObject {
    @Id
    private UUID uniqueId;
    private String name;
    private int age;
    @StorageSerialized
    private OtherObject otherObject;

    public TestObject(UUID uniqueId) {
        this.uniqueId = uniqueId;
        this.name = "John";
        this.age = 91;
        this.otherObject = new OtherObject();
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public OtherObject getOtherObject() {
        return otherObject;
    }

    public void setOtherObject(OtherObject otherObject) {
        this.otherObject = otherObject;
    }
}
