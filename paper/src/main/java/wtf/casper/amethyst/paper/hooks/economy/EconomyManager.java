package wtf.casper.amethyst.paper.hooks.economy;

import com.google.auto.service.AutoService;
import lombok.Getter;
import wtf.casper.amethyst.core.utils.ServiceUtil;
import wtf.casper.amethyst.paper.hooks.IHook;
import wtf.casper.amethyst.paper.hooks.IHookController;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@AutoService(IHookController.class)
public class EconomyManager implements IHookController {

    @Getter
    private final static Map<String, IEconomyType> economies = new HashMap<>();

    @Nullable
    public static IEconomyType getEconomy(String name) {
        if (economies.isEmpty()) {
            return null;
        }

        return economies.getOrDefault(name, null);
    }

    public static List<IEconomyType> getEconomies() {
        return List.copyOf(economies.values());
    }

    @Override
    public void registerHook(IHook hook) {
        if (hook instanceof IEconomyType economy) {
            if (!hook.canEnable()) {
                return;
            }

            economy.enable();
            economies.put(economy.getName(), economy);
        }
    }

    @Override
    public void unregisterHook(IHook hook) {
        if (hook instanceof IEconomyType economy) {
            if (economies.isEmpty()) {
                return;
            }

            economy.disable();
            economies.remove(economy);
        }
    }

    @Override
    public void unregisterAllHooks() {
        if (economies.isEmpty()) {
            return;
        }

        for (IEconomyType economy : economies.values()) {
            economy.disable();
        }

        economies.clear();
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

        List<IEconomyType> services = ServiceUtil.getServices(IEconomyType.class, this.getClass().getClassLoader());
        if (services.isEmpty()) {
            return;
        }

        for (IEconomyType economy : services) {
            if (economy.canEnable()) {
                economy.enable();
                economies.put(economy.getName(), economy);
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
