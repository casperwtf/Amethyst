package wtf.casper.amethyst.core.collections;

import java.util.ArrayList;

public class EvictingList<T> extends ArrayList<T> {
    private final int maxSize;

    public EvictingList(int maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    public boolean add(T t) {
        if (size() >= maxSize) {
            remove(0);
        }
        return super.add(t);
    }
}
