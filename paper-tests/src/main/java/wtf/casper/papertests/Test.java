package wtf.casper.papertests;

import java.util.Objects;

public interface Test {

    boolean test();

    default void assertVal(Object given, Object expected) {
        if (given == null && expected == null) {
            return;
        }

        if (!Objects.equals(given, expected)) {
            throw new AssertionError("Expected " + expected + " but got " + given);
        }
    }
}
