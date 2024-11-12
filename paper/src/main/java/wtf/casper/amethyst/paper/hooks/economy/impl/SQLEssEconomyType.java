package wtf.casper.amethyst.paper.hooks.economy.impl;

import com.google.auto.service.AutoService;
import net.craftersland.essentials.mysql.EMS;
import net.craftersland.essentials.mysql.storage.MysqlSetup;
import net.craftersland.essentials.mysql.storage.StorageHandler;
import org.bukkit.Bukkit;
import wtf.casper.amethyst.paper.hooks.economy.IEconomyType;
import wtf.casper.amethyst.paper.utils.GeyserUtils;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.UUID;

@AutoService(IEconomyType.class)
public class SQLEssEconomyType implements IEconomyType {

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
    public void deposit(String player, double amount, String currency) {
        if (amount < 0) {
            withdraw(player, -amount);
            return;
        }

        GeyserUtils.getUUID(player).ifPresent(uuid -> {
            modifyBalance(uuid, amount);
        });
    }

    @Override
    public void withdraw(String player, double amount, String currency) {
        if (amount < 0) {
            deposit(player, -amount);
            return;
        }

        GeyserUtils.getUUID(player).ifPresent(uuid -> {
            modifyBalance(uuid, -amount);
        });
    }

    @Override
    public void set(String player, double amount, String currency) {
        ems.setOfflineMoney(player, amount);
    }

    @Override
    public double get(String player, String currency) {
        Optional<UUID> optional = GeyserUtils.getUUID(player);

        if (optional.isPresent()) {
            return ems.getOfflineMoney(optional.get());
        }

        return ems.getOfflineMoney(Bukkit.getOfflinePlayer(player).getUniqueId());
    }

    @Override
    public boolean has(String player, double amount, String currency) {
        return get(player) >= amount;
    }

    @Override
    public void deposit(UUID player, double amount, String currency) {
        if (amount < 0) {
            withdraw(player, -amount);
            return;
        }

        modifyBalance(player, amount);
    }

    @Override
    public void withdraw(UUID player, double amount, String currency) {
        if (amount < 0) {
            deposit(player, -amount);
            return;
        }
        modifyBalance(player, -amount);
    }

    @Override
    public void set(UUID player, double amount, String currency) {
        ems.setOfflineMoney(player, amount);
    }

    @Override
    public double get(UUID player, String currency) {
        return ems.getOfflineMoney(player);
    }

    @Override
    public boolean has(UUID player, double amount, String currency) {
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
