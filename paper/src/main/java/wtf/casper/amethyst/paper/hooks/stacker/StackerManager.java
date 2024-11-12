package wtf.casper.amethyst.paper.hooks.stacker;

import com.google.auto.service.AutoService;
import lombok.Getter;
import lombok.extern.java.Log;
import wtf.casper.amethyst.core.utils.ServiceUtil;
import wtf.casper.amethyst.paper.hooks.IHook;
import wtf.casper.amethyst.paper.hooks.IHookController;

import java.util.HashSet;
import java.util.Set;

@AutoService(IHookController.class)
@Log
public class StackerManager implements IHookController {

    @Getter private static IStacker stacker = null;

    @Override
    public void registerHook(IHook hook) {
        if (!(hook instanceof IStacker)) {
            throw new RuntimeException("Hook must implement IStacker");
        }

        if (stacker != null) {
            return;
        }

        if (!hook.canEnable()) {
            return;
        }

        stacker = (IStacker) hook;
        stacker.enable();
    }

    @Override
    public void unregisterHook(IHook hook) {
        if (!(hook instanceof IStacker)) {
            throw new RuntimeException("Hook must implement IStacker");
        }

        if (stacker == null) {
            return;
        }

        if (!stacker.equals(hook)) {
            return;
        }

        stacker.disable();
        stacker = null;
    }

    @Override
    public void unregisterAllHooks() {
        if (stacker == null) {
            return;
        }

        stacker.disable();
        stacker = null;
    }

    @Override
    public void registerHooks(Set<IHook> hooks) {
        for (IHook hook : hooks) {
            registerHook(hook);
        }
    }

    @Override
    public void recalculateHooks() {
        unregisterAllHooks();
        registerHooks(new HashSet<>(ServiceUtil.getServices(IStacker.class, this.getClass().getClassLoader())));
    }

    @Override
    public void enable() {
        recalculateHooks();
    }

    @Override
    public void disable() {
        unregisterAllHooks();
    }
}
