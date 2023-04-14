package wtf.casper.papertests.tests.storagetest;

import lombok.ToString;

@ToString
public class OtherObject {
    private final String string;
    private final String string2;

    public OtherObject() {
        this.string = "Hello";
        this.string2 = "World";
    }

    public String getString() {
        return string;
    }

    public String getString2() {
        return string2;
    }
}
