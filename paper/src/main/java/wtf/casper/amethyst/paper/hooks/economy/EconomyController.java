package wtf.casper.amethyst.paper.hooks.economy;

import com.google.auto.service.AutoService;
import lombok.Getter;
import wtf.casper.amethyst.core.utils.ServiceUtil;
import wtf.casper.amethyst.paper.hooks.IHook;
import wtf.casper.amethyst.paper.hooks.IHookController;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AutoService(IHookController.class)
public class EconomyController implements IHookController {

    @Getter
    private final static Set<IEconomy> economies = new HashSet<>();

    @Nullable
    public static IEconomy getEconomy(String name) {
        for (IEconomy economy : economies) {
            if (economy.getName().equalsIgnoreCase(name)) {
                return economy;
            }
        }
        return null;
    }

    @Override
    public void registerHook(IHook hook) {
        if (hook instanceof IEconomy economy) {
            if (!hook.canEnable()) {
                return;
            }

            economy.enable();
            economies.add(economy);
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

        for (IEconomy economy : economies) {
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

        List<IEconomy> services = ServiceUtil.getServices(IEconomy.class);
        if (services.isEmpty()) {
            return;
        }

        for (IEconomy economy : services) {
            if (economy.canEnable()) {
                economy.enable();
                economies.add(economy);
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
        SQL_ESSENTIALS("SQLESS")
        ;

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
