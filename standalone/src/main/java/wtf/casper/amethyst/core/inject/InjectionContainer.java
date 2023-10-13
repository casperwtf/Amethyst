package wtf.casper.amethyst.core.inject;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

@Getter
public class InjectionContainer implements Comparable<InjectionContainer> {

    public static final InjectionContainer GLOBAL = new InjectionContainer(null, Integer.MAX_VALUE);

    @Nullable
    private InjectionContainer parent;
    /**
     * The priority of this container. Higher priority containers will be checked first.
     */
    private int priority;

    public InjectionContainer() {
        this(null, 0);
    }

    public InjectionContainer(@Nullable InjectionContainer parent, int priority) {
        this.parent = parent;
        this.priority = priority;
    }

    public InjectionContainer createChild(int priority) {
        return new InjectionContainer(this, priority);
    }

    @Override
    public int compareTo(@NotNull InjectionContainer o) {
        return Integer.compare(priority, o.priority);
    }
}
