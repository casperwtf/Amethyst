package wtf.casper.amethyst.paper.utils;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.List;

public class CommandUtil {

    public static void registerCommand(Plugin plugin, BukkitCommand executor) {
        CommandMap commandMap;
        try {
            final Field field = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            field.setAccessible(true);
            commandMap = (CommandMap) field.get(Bukkit.getServer());

        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        assert commandMap != null;
        commandMap.register(plugin.getName(), executor);
    }

    public static void registerCommand(String fallbackPrefix, BukkitCommand executor) {
        CommandMap commandMap;
        try {
            final Field field = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            field.setAccessible(true);
            commandMap = (CommandMap) field.get(Bukkit.getServer());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        assert commandMap != null;
        commandMap.register(fallbackPrefix, executor);
    }

    public static void unregisterCommand(String cmd, String label, List<String> alias) {
        CommandMap commandMap;
        try {
            final Field field = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            field.setAccessible(true);
            commandMap = (CommandMap) field.get(Bukkit.getServer());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        assert commandMap != null;
        commandMap.getKnownCommands().remove(cmd);
        for (String s : alias) {
            if (commandMap.getKnownCommands().containsKey(s) && commandMap.getKnownCommands().get(s).toString().contains(label)) {
                commandMap.getKnownCommands().remove(s);
            }
        }
    }

    public static void unregisterCommand(PluginCommand cmd) {
        CommandMap commandMap;
        try {
            final Field field = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            field.setAccessible(true);
            commandMap = (CommandMap) field.get(Bukkit.getServer());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        assert commandMap != null;
        commandMap.getKnownCommands().remove(cmd.getName());
        for (String alias : cmd.getAliases()) {
            if (commandMap.getKnownCommands().containsKey(alias) && commandMap.getKnownCommands().get(alias).toString().contains(cmd.getLabel())) {
                commandMap.getKnownCommands().remove(alias);
            }
        }
    }

    public static void unregisterCommand(BukkitCommand cmd) {
        CommandMap commandMap;
        try {
            final Field field = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            field.setAccessible(true);
            commandMap = (CommandMap) field.get(Bukkit.getServer());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        assert commandMap != null;
        commandMap.getKnownCommands().remove(cmd.getName());
        for (String alias : cmd.getAliases()) {
            if (commandMap.getKnownCommands().containsKey(alias) && commandMap.getKnownCommands().get(alias).toString().contains(cmd.getLabel())) {
                commandMap.getKnownCommands().remove(alias);
            }
        }
    }

    public static Command getCommand(String cmd) {
        CommandMap commandMap;
        try {
            final Field field = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            field.setAccessible(true);
            commandMap = (CommandMap) field.get(Bukkit.getServer());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        assert commandMap != null;
        return commandMap.getCommand(cmd);
    }

}
