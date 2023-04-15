package wtf.casper.amethyst.paper.command;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Deprecated
public abstract class AmethystSubCommand {

    private boolean playerOnly = false;
    private boolean consoleOnly = false;
    private String playerOnlyMessage = null;
    private String consoleOnlyMessage = null;
    private List<String> names = new ArrayList<>();

    public AmethystSubCommand(List<String> names) {
        for (String name : names) {
            this.names.add(name.toLowerCase());
        }
    }

    public abstract void execute(@NotNull CommandSender sender, String commandLabel, @NotNull String[] args);

    public abstract List<String> tabComplete(@NotNull CommandSender sender, String commandLabel, @NotNull String[] args);

    public Player asPlayer(@NotNull CommandSender sender) {
        if (sender instanceof Player) {
            return (Player) sender;
        }
        return null;
    }

    public AmethystCommand toAmethystCommand() {

        AmethystSubCommand amethystSubCommand = this;
        AmethystCommand amethystCommand = new AmethystCommand(names) {
            @Override
            public void runDefault(@NotNull CommandSender sender, String commandLabel, @NotNull String[] args) {
                amethystSubCommand.execute(sender, commandLabel, args);
            }

            @Override
            public List<String> defaultTab(@NotNull CommandSender sender, String commandLabel, @NotNull String[] args) {
                return amethystSubCommand.tabComplete(sender, commandLabel, args);
            }
        };

        amethystCommand.setConsoleOnly(consoleOnly);
        amethystCommand.setConsoleOnlyMessage(consoleOnlyMessage);
        amethystCommand.setPlayerOnly(playerOnly);
        amethystCommand.setPlayerOnlyMessage(playerOnlyMessage);

        return amethystCommand;
    }
}
