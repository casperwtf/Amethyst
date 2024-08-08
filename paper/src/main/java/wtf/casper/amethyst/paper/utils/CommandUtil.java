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

    private static Field commandMapField;
    private static CommandMap commandMap;

    private CommandUtil() {
        throw new IllegalStateException("Utility class");
    }

    static {
        try {
            commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Register a command with the server
     * @param plugin Plugin to register the command to
     * @param executor Command to register
     */
    public static void registerCommand(Plugin plugin, BukkitCommand executor) {
        assert commandMap != null;
        commandMap.register(plugin.getName().toLowerCase(), executor);
    }

    /**
     * Register a command with the server
     * @param fallbackPrefix Fallback prefix to register the command to
     * @param executor Command to register
     */
    public static void registerCommand(String fallbackPrefix, BukkitCommand executor) {
        assert commandMap != null;
        commandMap.register(fallbackPrefix, executor);
    }

    /**
     * Unregister a command from the server
     * @param cmd Command to unregister
     * @param label Label of the command
     * @param alias Aliases of the command
     */
    public static void unregisterCommand(String cmd, String label, List<String> alias) {
        assert commandMap != null;
        commandMap.getKnownCommands().remove(cmd);
        for (String s : alias) {
            if (commandMap.getKnownCommands().containsKey(s) && commandMap.getKnownCommands().get(s).toString().contains(label)) {
                commandMap.getKnownCommands().remove(s);
            }
        }
    }

    /**
     * Unregister a command from the server
     * @param cmd Command to unregister
     */
    public static void unregisterCommand(PluginCommand cmd) {
        assert commandMap != null;
        commandMap.getKnownCommands().remove(cmd.getName());
        for (String alias : cmd.getAliases()) {
            if (commandMap.getKnownCommands().containsKey(alias) && commandMap.getKnownCommands().get(alias).toString().contains(cmd.getLabel())) {
                commandMap.getKnownCommands().remove(alias);
            }
        }
    }

    /**
     * Unregister a command from the server
     * @param cmd Command to unregister
     */
    public static void unregisterCommand(BukkitCommand cmd) {
        assert commandMap != null;
        commandMap.getKnownCommands().remove(cmd.getName());
        for (String alias : cmd.getAliases()) {
            if (commandMap.getKnownCommands().containsKey(alias) && commandMap.getKnownCommands().get(alias).toString().contains(cmd.getLabel())) {
                commandMap.getKnownCommands().remove(alias);
            }
        }
    }

    /**
     * Get a command from the server
     * @param cmd Command to get
     * @return The command
     */
    public static Command getCommand(String cmd) {
        assert commandMap != null;
        return commandMap.getCommand(cmd);
    }

}
