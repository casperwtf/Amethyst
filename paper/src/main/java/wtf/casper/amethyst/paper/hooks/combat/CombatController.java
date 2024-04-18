package wtf.casper.amethyst.paper.hooks.combat;

import com.google.auto.service.AutoService;
import lombok.Getter;
import wtf.casper.amethyst.core.utils.ServiceUtil;
import wtf.casper.amethyst.paper.hooks.IHook;
import wtf.casper.amethyst.paper.hooks.IHookController;

import java.util.List;
import java.util.Set;

@AutoService(IHookController.class)
public class CombatController implements IHookController {

    @Getter private static ICombat hook = null;

    @Override
    public void registerHook(IHook hook) {
        if (hook instanceof ICombat) {
            if (!hook.canEnable()) {
                return;
            }

            CombatController.hook = (ICombat) hook;
        }
    }

    @Override
    public void unregisterHook(IHook hook) {
        if (hook instanceof ICombat) {
            if (CombatController.hook == null) {
                return;
            }

            if (CombatController.hook == hook) {
                CombatController.hook = null;
            }
        }
    }

    @Override
    public void unregisterAllHooks() {
        CombatController.hook = null;
    }

    @Override
    public void registerHooks(Set<IHook> hooks) {
        for (IHook hook : hooks) {
            registerHook(hook);
        }
    }

    @Override
    public void recalculateHooks() {
        if (hook != null) {
            hook.disable();
        }

        List<ICombat> services = ServiceUtil.getServices(ICombat.class, this.getClass().getClassLoader());
        if (services.isEmpty()) {
            return;
        }

        for (ICombat service : services) {
            if (service.canEnable()) {
                service.enable();
                hook = service;
                break;
            }
        }
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
