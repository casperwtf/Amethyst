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
public class EconomyController implements IHookController {

    @Getter
    private final static Map<String, IEconomy> economies = new HashMap<>();

    @Nullable
    public static IEconomy getEconomy(String name) {
        if (economies.isEmpty()) {
            return null;
        }

        return economies.getOrDefault(name, null);
    }

    @Override
    public void registerHook(IHook hook) {
        if (hook instanceof IEconomy economy) {
            if (!hook.canEnable()) {
                return;
            }

            economy.enable();
            economies.put(economy.getName(), economy);
        }
    }

    @Override
    public void unregisterHook(IHook hook) {
        if (hook instanceof IEconomy economy) {
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

        for (IEconomy economy : economies.values()) {
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

        List<IEconomy> services = ServiceUtil.getServices(IEconomy.class, this.getClass().getClassLoader());
        if (services.isEmpty()) {
            return;
        }

        for (IEconomy economy : services) {
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

    public enum Economies {
        EXPERIENCE("EXP"),
        VAULT("VAULT"),
        SQL_ESSENTIALS("SQLESS");

        private final String name;

        Economies(String name) {
            this.name = name;
        }

        @Nullable
        public IEconomy getEconomy() {
            return EconomyController.getEconomy(name);
        }
    }
}
