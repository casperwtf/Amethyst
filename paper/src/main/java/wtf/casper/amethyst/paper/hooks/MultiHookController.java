package wtf.casper.amethyst.paper.hooks;

import wtf.casper.amethyst.core.obj.Pair;

import java.util.*;

public abstract class MultiHookController<K, T extends IHook> implements IHookController {

    private final Map<K, T> hookMap = new HashMap<>();
    private final Set<IHook> disabledHooks = new HashSet<>();

    public void registerHook(K key, T hook) {
        if (hook.canEnable()) {
            hookMap.put(key, hook);
            hook.enable();
        } else {
            disabledHooks.add(hook);
        }
    }

    public void unregisterHook(T hook) {
        hook.disable();
        hookMap.values().remove(hook);
        disabledHooks.remove(hook);
    }

    public void unregisterHook(K key) {
        T hook = hookMap.remove(key);
        if (hook != null) {
            hook.disable();
            disabledHooks.remove(hook);
        }
    }

    public void unregisterAllHooks() {
        for (T hook : hookMap.values()) {
            hook.disable();
        }
        hookMap.clear();
        disabledHooks.clear();
    }

    public void registerHooks(Iterable<Pair<K, T>> hooks) {
        for (Pair<K, T> hook : hooks) {
            registerHook(hook.getFirst(), hook.getSecond());
        }
    }

    public abstract void enable();

    public abstract void disable();
}
