package wtf.casper.amethyst.paper.command;

import com.google.common.collect.ImmutableList;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wtf.casper.amethyst.paper.utils.CommandUtil;
import wtf.casper.amethyst.paper.utils.PlaceholderReplacer;
import wtf.casper.amethyst.paper.utils.StringUtilsPaper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public abstract class AmethystCommand extends BukkitCommand {

    private final List<AmethystCommand> subCommands = new ArrayList<>();
    private final List<String> names = new ArrayList<>();
    @Nullable
    private JavaPlugin plugin = null;
    private boolean playerOnly = false;
    private boolean consoleOnly = false;
    private String playerOnlyMessage = null;

    private String consoleOnlyMessage = null;
    private String noPermissionMessage = null;
    private Section noPermissionMessageSection = null;
    private PlaceholderReplacer noPermissionMessageReplacer = new PlaceholderReplacer();

    public AmethystCommand(@NotNull String name) {
        super(name);
        this.names.add(name);
    }

    public AmethystCommand(List<String> strings) {
        super(strings.get(0));
        if (strings.size() > 1) {
            setAliases(strings.subList(1, strings.size()));
        }
        this.names.addAll(strings);
    }

    public AmethystCommand(String... strings) {
        super(strings[0]);
        if (strings.length > 1) {
            List<String> aliases = new ArrayList<>(Arrays.asList(strings).subList(1, strings.length));
            setAliases(aliases);
        }
        this.names.addAll(Arrays.asList(strings));
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (plugin != null && !plugin.isEnabled()) {
            return true;
        }

        if (playerOnly && !(sender instanceof Player)) {
            if (playerOnlyMessage != null) {
                sender.sendMessage(playerOnlyMessage);
            }
            return true;
        }

        if (consoleOnly && sender instanceof Player) {
            if (consoleOnlyMessage != null) {
                sender.sendMessage(consoleOnlyMessage);
            }
            return true;
        }

        if (sender instanceof Player) {
            Player player = asPlayer(sender);
            if (getPermission() != null && !getPermission().isEmpty() && !player.hasPermission(getPermission())) {
                if (noPermissionMessage != null) {
                    player.sendMessage(noPermissionMessageReplacer.replace(noPermissionMessage));
                } else if (noPermissionMessageSection != null) {
                    StringUtilsPaper.sendMessage(noPermissionMessageSection, player, noPermissionMessageReplacer);
                }
                return true;
            }
        }

        if (subCommands.isEmpty()) {
            runDefault(sender, commandLabel, args);
            return true;
        }

        if (args.length == 0) {
            runDefault(sender, commandLabel, args);
            return true;
        }

        AmethystCommand subCommand = findSubCommand(args[0]);
        if (subCommand == null) {
            runDefault(sender, commandLabel, args);
            return true;
        }

        if (subCommand.isPlayerOnly() && !(sender instanceof Player)) {
            if (subCommand.getPlayerOnlyMessage() != null) {
                sender.sendMessage(subCommand.getPlayerOnlyMessage());
            }
            return true;
        }

        if (subCommand.isConsoleOnly() && sender instanceof Player) {
            if (subCommand.getConsoleOnlyMessage() != null) {
                sender.sendMessage(subCommand.getConsoleOnlyMessage());
            }
            return true;
        }

        subCommand.runDefault(sender, commandLabel, removeElement(args, 0));
        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {

        if (subCommands.isEmpty()) {
            List<String> strings = defaultTab(sender, alias, args);
            return strings == null ? ImmutableList.of() : strings;
        }

        if (args.length == 0) {
            return subCommands.stream().filter(amethystCommand -> {
                if (amethystCommand.getPermission() != null && !amethystCommand.getPermission().isEmpty()) {
                    return sender.hasPermission(amethystCommand.getPermission());
                }
                return true;
            }).flatMap(amethystSubCommand -> amethystSubCommand.getNames().stream()).collect(Collectors.toList());
        }

        AmethystCommand subCommand = findSubCommand(args[0]);
        if (subCommand == null) {
            return subCommands.stream().filter(amethystCommand -> {
                if (amethystCommand.getPermission() != null && !amethystCommand.getPermission().isEmpty()) {
                    return sender.hasPermission(amethystCommand.getPermission());
                }
                return true;
            }).flatMap(amethystSubCommand -> amethystSubCommand.getNames().stream()).collect(Collectors.toList());
        }

        List<String> strings = subCommand.defaultTab(sender, alias, removeElement(args, 0));
        return strings == null ? ImmutableList.of() : strings;
    }

    public void runDefault(@NotNull CommandSender sender, String label, @NotNull String[] args) {
    };

    public List<String> defaultTab(@NotNull CommandSender sender, String commandLabel, @NotNull String[] args) {
        return null;
    }

    public void addAlias(@NotNull String alias) {
        getAliases().add(alias);
    }

    public Player asPlayer(@NotNull CommandSender sender) {
        if (sender instanceof Player) {
            return (Player) sender;
        }
        return null;
    }

    public void register(Plugin plugin) {
        CommandUtil.registerCommand(plugin, this);
        this.plugin = (JavaPlugin) plugin;
    }

    public void register(String prefix) {
        CommandUtil.registerCommand(prefix, this);
    }

    public void unregister() {
        CommandUtil.unregisterCommand(this);
    }

    public AmethystCommand findSubCommand(String arg) {
        for (AmethystCommand subCommand : subCommands) {
            if (subCommand.getNames().contains(arg.toLowerCase())) {
                return subCommand;
            }
        }
        return null;
    }

    @Deprecated
    public void registerSubCommand(AmethystSubCommand subCommand) {
        AmethystCommand e = subCommand.toAmethystCommand();
        e.setPlugin(plugin);
        subCommands.add(e);
    }

    @Deprecated
    public void setRequiredPermission(@NotNull String permission) {
        setPermission(permission);
    }

    public void registerSubCommand(AmethystCommand subCommand) {
        subCommand.setPlugin(plugin);
        subCommands.add(subCommand);
    }

    public void unregisterSubCommand(AmethystCommand subCommand) {
        subCommands.remove(subCommand);
    }

    public void unregisterSubCommand(String name) {
        for (AmethystCommand subCommand : subCommands) {
            if (subCommand.getNames().contains(name)) {
                subCommands.remove(subCommand);
                return;
            }
        }
    }

    private String[] removeElement(String[] array, int index) {
        String[] newArray = new String[array.length - 1];
        for (int i = 0; i < array.length; i++) {
            if (i == index) continue;
            newArray[i < index ? i : i - 1] = array[i];
        }
        return newArray;
    }

    public <T extends JavaPlugin> T getPlugin(Class<T> plugin) {
        return JavaPlugin.getPlugin(plugin);
    }
}
