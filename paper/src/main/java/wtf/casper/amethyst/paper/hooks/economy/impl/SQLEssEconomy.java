package wtf.casper.amethyst.paper.hooks.economy.impl;

import net.craftersland.essentials.mysql.EMS;
import net.craftersland.essentials.mysql.storage.MysqlSetup;
import net.craftersland.essentials.mysql.storage.StorageHandler;
import org.bukkit.Bukkit;
import org.geysermc.floodgate.api.FloodgateApi;
import wtf.casper.amethyst.paper.hooks.economy.IEconomy;
import wtf.casper.amethyst.paper.utils.GeyserUtils;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.UUID;

public class SQLEssEconomy implements IEconomy {

    private StorageHandler ems;
    private MysqlSetup mysqlSetup;

    @Override
    public boolean canEnable() {
        return Bukkit.getPluginManager().isPluginEnabled("EssentialsMysqlStorage");
    }

    @Override
    public void enable() {
        Field field;
        try {
            field = EMS.class.getDeclaredField("eH");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        Field field2;
        try {
            field2 = EMS.class.getDeclaredField("ms");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }

        field.setAccessible(true);
        field2.setAccessible(true);

        try {
            ems = (StorageHandler) field.get(null);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        try {
            mysqlSetup = (MysqlSetup) field2.get(null);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void disable() {

    }

    @Override
    public String getName() {
        return "SQLESS";
    }

    @Override
    public void deposit(String player, double amount) {
        if (amount < 0) {
            withdraw(player, -amount);
            return;
        }

        if (GeyserUtils.isFloodgateEnabled()) {
            UUID uuid = FloodgateApi.getInstance().getUuidFor(player).join();
            if (uuid == null) {
                modifyBalance(Bukkit.getOfflinePlayer(player).getUniqueId(), amount);
                return;
            }
            modifyBalance(uuid, amount);
        } else {
            modifyBalance(Bukkit.getOfflinePlayer(player).getUniqueId(), amount);
        }
    }

    @Override
    public void withdraw(String player, double amount) {
        if (amount < 0) {
            deposit(player, -amount);
            return;
        }

        if (GeyserUtils.isFloodgateEnabled()) {
            UUID uuid = FloodgateApi.getInstance().getUuidFor(player).join();
            if (uuid == null) {
                modifyBalance(Bukkit.getOfflinePlayer(player).getUniqueId(), -amount);
                return;
            }
            modifyBalance(uuid, -amount);
        } else {
            modifyBalance(Bukkit.getOfflinePlayer(player).getUniqueId(), -amount);
        }
    }

    @Override
    public void set(String player, double amount) {
        ems.setOfflineMoney(player, amount);
    }

    @Override
    public double get(String player) {
        if (GeyserUtils.isFloodgateEnabled()) {
            UUID uuid = FloodgateApi.getInstance().getUuidFor(player).join();
            return ems.getOfflineMoney(Objects.requireNonNullElseGet(uuid, () -> Bukkit.getOfflinePlayer(player).getUniqueId()));
        }

        return ems.getOfflineMoney(Bukkit.getOfflinePlayer(player).getUniqueId());
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

        modifyBalance(player, amount);
    }

    @Override
    public void withdraw(UUID player, double amount) {
        if (amount < 0) {
            deposit(player, -amount);
            return;
        }
        modifyBalance(player, -amount);
    }

    @Override
    public void set(UUID player, double amount) {
        ems.setOfflineMoney(player, amount);
    }

    @Override
    public double get(UUID player) {
        return ems.getOfflineMoney(player);
    }

    @Override
    public boolean has(UUID player, double amount) {
        return get(player) >= amount;
    }

    private void modifyBalance(UUID user, double amount) {
        if (!ems.hasAccount(mysqlSetup.getConnection(MysqlSetup.DatabaseConnections.SendData), user)) {
            return;
        }

        double usersMoney = ems.getOfflineMoney(user);
        double newMoney = usersMoney + amount;

        ems.setOfflineMoney(user, newMoney);
    }
}
