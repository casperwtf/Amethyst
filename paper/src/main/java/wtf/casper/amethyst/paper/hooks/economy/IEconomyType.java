package wtf.casper.amethyst.paper.hooks.economy;

import wtf.casper.amethyst.paper.hooks.IHook;

import java.util.UUID;

public interface IEconomyType extends IHook {

    String getName();

    default void deposit(String player, double amount) {
        deposit(player, amount, null);
    }

    default void withdraw(String player, double amount) {
        withdraw(player, amount, null);
    }

    default void set(String player, double amount) {
        set(player, amount, null);
    }

    default double get(String player) {
        return get(player, null);
    }

    default boolean has(String player, double amount) {
        return has(player, amount, null);
    }

    default void deposit(UUID player, double amount) {
        deposit(player, amount, null);
    }

    default void withdraw(UUID player, double amount) {
        withdraw(player, amount, null);
    }

    default void set(UUID player, double amount) {
        set(player, amount, null);
    }

    default double get(UUID player) {
        return get(player, null);
    }

    default boolean has(UUID player, double amount) {
        return has(player, amount, null);
    }

    void deposit(String player, double amount, String currency);

    void withdraw(String player, double amount, String currency);

    void set(String player, double amount, String currency);

    double get(String player, String currency);

    boolean has(String player, double amount, String currency);

    void deposit(UUID player, double amount, String currency);

    void withdraw(UUID player, double amount, String currency);

    void set(UUID player, double amount, String currency);

    double get(UUID player, String currency);

    boolean has(UUID player, double amount, String currency);

}
