package wtf.casper.amethyst.paper.hooks.economy;

import wtf.casper.amethyst.paper.hooks.IHook;

import java.util.UUID;

public interface IEconomy extends IHook {

    String getName();

    void deposit(String player, double amount);

    void withdraw(String player, double amount);

    void set(String player, double amount);

    double get(String player);

    boolean has(String player, double amount);

    void deposit(UUID player, double amount);

    void withdraw(UUID player, double amount);

    void set(UUID player, double amount);

    double get(UUID player);

    boolean has(UUID player, double amount);
}
