package wtf.casper.amethyst.paper.hooks.economy.impl;

import com.google.auto.service.AutoService;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import wtf.casper.amethyst.paper.hooks.economy.IEconomyType;

import java.util.UUID;

@AutoService(IEconomyType.class)
public class VaultEconomyType implements IEconomyType {

    private Economy economy;

    @Override
    public boolean canEnable() {
        return Bukkit.getPluginManager().isPluginEnabled("Vault");
    }

    @Override
    public void enable() {
        setupEconomy();
    }

    @Override
    public void disable() {

    }

    @Override
    public void deposit(String player, double amount, String currency) {
        if (amount < 0) {
            withdraw(player, -amount);
            return;
        }
        economy.depositPlayer(Bukkit.getOfflinePlayer(player), amount);
    }

    @Override
    public void withdraw(String player, double amount, String currency) {
        if (amount < 0) {
            deposit(player, -amount);
            return;
        }
        economy.withdrawPlayer(Bukkit.getOfflinePlayer(player), amount);
    }

    @Override
    public void set(String player, double amount, String currency) {
        double balance = get(player);
        if (balance > amount) {
            withdraw(player, balance - amount);
        } else if (balance < amount) {
            deposit(player, amount - balance);
        }
    }

    @Override
    public double get(String player, String currency) {
        return economy.getBalance(Bukkit.getOfflinePlayer(player));
    }

    @Override
    public boolean has(String player, double amount, String currency) {
        return get(player) >= amount;
    }

    @Override
    public void deposit(UUID player, double amount, String currency) {
        if (0 > amount) {
            withdraw(player, -amount);
            return;
        }
        economy.depositPlayer(Bukkit.getOfflinePlayer(player), amount);
    }

    @Override
    public void withdraw(UUID player, double amount, String currency) {
        if (0 > amount) {
            deposit(player, -amount);
            return;
        }
        economy.withdrawPlayer(Bukkit.getOfflinePlayer(player), amount);
    }

    @Override
    public void set(UUID player, double amount, String currency) {
        double balance = get(player);
        if (balance > amount) {
            withdraw(player, balance - amount);
        } else if (balance < amount) {
            deposit(player, amount - balance);
        }
    }

    @Override
    public double get(UUID player, String currency) {
        return economy.getBalance(Bukkit.getOfflinePlayer(player));
    }

    @Override
    public boolean has(UUID player, double amount, String currency) {
        return get(player) >= amount;
    }

    @Override
    public String getName() {
        return "VAULT";
    }

    private void setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return;
        }
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return;
        }
        economy = rsp.getProvider();
    }
}
