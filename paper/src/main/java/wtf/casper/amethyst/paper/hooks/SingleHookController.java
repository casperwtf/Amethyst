package wtf.casper.amethyst.paper.hooks;

import java.util.ArrayList;
import java.util.List;

public abstract class SingleHookController<T extends IHook> implements IHookController {

    protected T hook;
    protected final List<T> hooks = new ArrayList<>();

    public T hook() {
        return hook;
    }

    public void hook(T hook) {
        this.hook = hook;
    }

    public void registerHook(T hook) {
        hooks.add(hook);
        if (this.hook == null) {
            this.hook = hook;
        } else {
            recalculateHooks();
        }
    }

    public void unregisterHook(T hook) {
        hooks.remove(hook);
    }

    public void unregisterAllHooks() {
        hooks.clear();
    }

    public void registerHooks(Iterable<T> hooks) {
        hooks.forEach(hook -> {
            if (hook != null) {
                registerHook(hook);
            }
        });
    }

    public void recalculateHooks() {
        hook = null;
        hooks.forEach(hook -> {
            if (this.hook == null || hook.priority() > this.hook.priority()) {
                this.hook = hook;
            }
        });
    }

    public abstract void enable();

    public abstract void disable();
}
