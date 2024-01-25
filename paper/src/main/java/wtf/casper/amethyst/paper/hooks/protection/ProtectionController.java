package wtf.casper.amethyst.paper.hooks.protection;

import com.google.auto.service.AutoService;
import lombok.Getter;
import lombok.extern.java.Log;
import wtf.casper.amethyst.core.utils.ServiceUtil;
import wtf.casper.amethyst.paper.hooks.IHook;
import wtf.casper.amethyst.paper.hooks.IHookController;

import java.util.List;
import java.util.Set;

@AutoService(IHookController.class)
@Log
public class ProtectionController implements IHookController {

    @Getter private static IProtection protections;

    @Override
    public void registerHook(IHook hook) {
        if (!(hook instanceof IProtection protection)) {
            throw new RuntimeException("Hook must implement IProtection");
        }

        if (!protection.canEnable()) {
            return;
        }

        protection.enable();
        ProtectionController.protections = protection;
        log.info("Registered protection: " + protection.getClass().getSimpleName());
    }

    @Override
    public void unregisterHook(IHook hook) {
        if (!(hook instanceof IProtection)) {
            throw new RuntimeException("Hook must implement IProtection");
        }

        if (protections == null) {
            return;
        }

        if (!protections.equals(hook)) {
            return;
        }

        protections.disable();
        protections = null;
        log.info("Unregistered protection: " + hook.getClass().getSimpleName());
    }

    @Override
    public void unregisterAllHooks() {
        if (protections == null) {
            return;
        }

        protections.disable();
        protections = null;
        log.info("Unregistered all protections");
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
        List<IProtection> list = ServiceUtil.getServices(IProtection.class);

        for (IProtection protection : list) {
            if (protection.canEnable()) {
                registerHook(protection);
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
