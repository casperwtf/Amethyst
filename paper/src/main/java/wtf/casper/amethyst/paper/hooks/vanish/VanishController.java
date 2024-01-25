package wtf.casper.amethyst.paper.hooks.vanish;

import com.google.auto.service.AutoService;
import lombok.Getter;
import wtf.casper.amethyst.core.utils.ServiceUtil;
import wtf.casper.amethyst.paper.hooks.IHook;
import wtf.casper.amethyst.paper.hooks.IHookController;

import java.util.Set;

@AutoService(IHookController.class)
public class VanishController implements IHookController {

    @Getter private static IVanish hook = null;

    @Override
    public void registerHook(IHook hook) {
        if (hook instanceof IVanish) {
            if (!hook.canEnable()) {
                return;
            }

            VanishController.hook = (IVanish) hook;
        }
    }

    @Override
    public void unregisterHook(IHook hook) {
        if (hook instanceof IVanish) {
            if (VanishController.hook == null) {
                return;
            }

            if (VanishController.hook == hook) {
                VanishController.hook = null;
            }
        }
    }

    @Override
    public void unregisterAllHooks() {
        VanishController.hook = null;
    }

    @Override
    public void registerHooks(Set<IHook> hooks) {
        for (IHook hook : hooks) {
            registerHook(hook);
        }
    }

    @Override
    public void recalculateHooks() {
        ServiceUtil.getServices(IVanish.class).forEach(this::registerHook);
    }

    @Override
    public void enable() {

    }

    @Override
    public void disable() {
        unregisterAllHooks();
    }
}
