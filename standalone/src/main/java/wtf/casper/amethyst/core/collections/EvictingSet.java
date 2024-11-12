package wtf.casper.amethyst.core.collections;

import java.util.HashSet;
import java.util.Iterator;

public class EvictingSet<T> extends HashSet<T> {
    private final int maxSize;

    public EvictingSet(int maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    public boolean add(T t) {
        if (size() >= maxSize) {
            Iterator<T> iterator = iterator();
            if (iterator.hasNext()) {
                iterator.next();
                iterator.remove();
            }
        }
        return super.add(t);
    }
}
