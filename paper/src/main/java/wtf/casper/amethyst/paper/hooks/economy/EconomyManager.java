package wtf.casper.amethyst.paper.hooks.economy;

import org.jetbrains.annotations.Nullable;
import wtf.casper.amethyst.paper.hooks.economy.impl.ExpEconomy;
import wtf.casper.amethyst.paper.hooks.economy.impl.SQLEssEconomy;
import wtf.casper.amethyst.paper.hooks.economy.impl.VaultEconomy;

import java.util.ArrayList;
import java.util.List;

public class EconomyManager {

    public final static List<IEconomy> economies = new ArrayList<>();

    public static void init() {
        for (IEconomy iEconomy : List.of(
                new ExpEconomy(),
                new SQLEssEconomy(),
                new VaultEconomy()
        )) {
            if (iEconomy.canEnable()) {
                iEconomy.enable();
                economies.add(iEconomy);
            }
        }
    }

    @Nullable
    public static IEconomy getEconomy(String name) {
        for (IEconomy iEconomy : economies) {
            if (iEconomy.getName().equalsIgnoreCase(name)) {
                return iEconomy;
            }
        }
        return null;
    }

    public static void disable() {
        for (IEconomy iEconomy : economies) {
            iEconomy.disable();
        }
    }

    public enum Economies {
        EXP("EXP"),
        SQLESS("SQLESS"),
        VAULT("VAULT");

        private final String name;

        Economies(String name) {
            this.name = name;
        }

        @Nullable
        IEconomy getEconomy() {
            return EconomyManager.getEconomy(name);
        }
    }

}
