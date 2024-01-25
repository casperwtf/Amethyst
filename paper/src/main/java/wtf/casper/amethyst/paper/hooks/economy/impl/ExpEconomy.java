package wtf.casper.amethyst.paper.hooks.economy.impl;

import com.google.auto.service.AutoService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import wtf.casper.amethyst.paper.hooks.economy.IEconomy;
import wtf.casper.amethyst.paper.utils.ExperienceUtils;

import java.util.UUID;

@AutoService(IEconomy.class)
public class ExpEconomy implements IEconomy {
    @Override
    public boolean canEnable() {
        return true;
    }

    @Override
    public void enable() {

    }

    @Override
    public void disable() {

    }

    @Override
    public String getName() {
        return "EXP";
    }

    @Override
    public void deposit(String player, double amount) {
        if (amount < 0) {
            withdraw(player, -amount);
            return;
        }

        Player player1 = Bukkit.getPlayer(player);
        ExperienceUtils.changeExp(player1, (int) amount);
    }

    @Override
    public void withdraw(String player, double amount) {
        if (amount < 0) {
            deposit(player, -amount);
            return;
        }

        Player player1 = Bukkit.getPlayer(player);
        ExperienceUtils.changeExp(player1, -(int) amount);
    }

    @Override
    public void set(String player, double amount) {
        double balance = get(player);
        if (balance > amount) {
            withdraw(player, balance - amount);
        } else if (balance < amount) {
            deposit(player, amount - balance);
        }
    }

    @Override
    public double get(String player) {
        Player player1 = Bukkit.getPlayer(player);
        if (player1 == null) return 0;
        return ExperienceUtils.getExp(player1);
    }

    @Override
    public boolean has(String player, double amount) {
        return get(player) >= amount;
    }

    @Override
    public void deposit(UUID player, double amount) {
        if (amount < 0) {
            withdraw(player, -amount);
            return;
        }

        Player player1 = Bukkit.getPlayer(player);
        ExperienceUtils.changeExp(player1, (int) amount);
    }

    @Override
    public void withdraw(UUID player, double amount) {
        if (amount < 0) {
            deposit(player, -amount);
            return;
        }

        Player player1 = Bukkit.getPlayer(player);
        ExperienceUtils.changeExp(player1, -(int) amount);
    }

    @Override
    public void set(UUID player, double amount) {
        double balance = get(player);
        if (balance > amount) {
            withdraw(player, balance - amount);
        } else if (balance < amount) {
            deposit(player, amount - balance);
        }
    }

    @Override
    public double get(UUID player) {
        Player player1 = Bukkit.getPlayer(player);
        if (player1 == null) return 0;
        return ExperienceUtils.getExp(player1);
    }

    @Override
    public boolean has(UUID player, double amount) {
        return get(player) >= amount;
    }
}
