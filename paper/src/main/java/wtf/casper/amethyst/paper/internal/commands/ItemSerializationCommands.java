package wtf.casper.amethyst.paper.internal.commands;


import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import wtf.casper.amethyst.paper.command.AmethystCommand;
import wtf.casper.amethyst.paper.items.ItemFromString;

public class ItemSerializationCommands extends AmethystCommand {

    public ItemSerializationCommands() {
        super("itemserialize");
    }

    @Override
    public void runDefault(@NotNull CommandSender sender, String label, @NotNull String[] args) {
        if (!sender.hasPermission("amethyst.command.item.give")) {
            return;
        }

        if (args.length == 0) {
            sender.sendMessage("Usage: /itemserialize <serialize|deserialize> <item>");
            return;
        }

        if (args[0].equalsIgnoreCase("serialize")) {
            ItemStack stack = ((Player) sender).getInventory().getItemInMainHand();
            String serialized = ItemFromString.serialize(stack);
            sender.sendMessage(serialized);
        } else if (args[0].equalsIgnoreCase("deserialize")) {
            if (args.length == 1) {
                sender.sendMessage("Usage: /itemserialize deserialize <item>");
                return;
            }

            StringBuilder builder = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                builder.append(args[i]);
                builder.append(" ");
            }
            asPlayer(sender).sendMessage("\"" + builder.toString().trim() + "\"" + " is being deserialized...");
            ItemStack stack = ItemFromString.deserialize(builder.toString().trim());
            ((Player) sender).getInventory().addItem(stack);
        }
    }
}
