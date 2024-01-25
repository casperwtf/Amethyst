package wtf.casper.amethyst.paper.hooks;

import java.util.Set;

public interface IHookController {
    void registerHook(IHook hook);
    void unregisterHook(IHook hook);
    void unregisterAllHooks();
    void registerHooks(Set<IHook> hooks);
    void recalculateHooks();
    void enable();
    void disable();
}
