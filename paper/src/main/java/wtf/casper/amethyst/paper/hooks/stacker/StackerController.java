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
public class StackerController implements IHookController {

    @Getter private static IStacker stacker = null;

    @Override
    public void registerHook(IHook hook) {
        if (!(hook instanceof IStacker)) {
            throw new RuntimeException("Hook must implement IStacker");
        }

        IStacker stacker = (IStacker) hook;
        if (!stacker.canEnable()) {
            return;
        }

        stacker.enable();
        StackerController.stacker = stacker;
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
        registerHooks(new HashSet<>(ServiceUtil.getServices(IStacker.class)));
    }

    @Override
    public void enable() {
        recalculateHooks();
        log.info("Stacker hook enabled: " + stacker.getClass().getSimpleName());
    }

    @Override
    public void disable() {
        unregisterAllHooks();
        log.info("Stacker hook disabled.");
    }
}
