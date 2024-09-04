package wtf.casper.amethyst.paper.providers;

import cloud.commandframework.CommandManager;
import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator;
import cloud.commandframework.extra.confirmation.CommandConfirmationManager;
import cloud.commandframework.meta.SimpleCommandMeta;
import cloud.commandframework.paper.PaperCommandManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import wtf.casper.amethyst.core.inject.Inject;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Getter
@Setter
public class CloudCommandProvider {

    private PaperCommandManager<CommandSender> commandManager;

    public void setup(JavaPlugin plugin) {
        try {
            commandManager = new PaperCommandManager(
                    plugin,
                    AsynchronousCommandExecutionCoordinator.builder()
                            .withSynchronousParsing()
                            .build(),
                    Function.identity(),
                    Function.identity()
            );

            if (commandManager.queryCapability(CloudBukkitCapabilities.BRIGADIER)) {
                plugin.getLogger().log(java.util.logging.Level.INFO, "Enabling Brigadier support for Amethyst");
                commandManager.registerBrigadier();
            }

            if (commandManager.queryCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
                plugin.getLogger().log(java.util.logging.Level.INFO, "Enabling asynchronous tab completion for Amethyst");
                commandManager.registerAsynchronousCompletions();
            }

            AnnotationParser<CommandSender> parser = new AnnotationParser<>(commandManager, CommandSender.class,
                    parserParameters -> SimpleCommandMeta.empty()
            );

            CommandConfirmationManager<CommandSender> commandConfirmationManager = new CommandConfirmationManager<>(
                    30,
                    TimeUnit.SECONDS,
                    ctx -> {
                        StringBuilder builder = new StringBuilder();
                        builder.append("/");
                        for (CommandArgument<CommandSender, ?> argument : ctx.getCommand().getArguments()) {
                            if (argument.getOwningCommand() == null) {
                                builder.append(argument.getName()).append(" ");
                            } else {
                                break;
                            }
                        }

                        builder.append("confirm");

                        //TODO: make translatable?
                        ctx.getCommandContext().getSender().sendMessage(ChatColor.RED + "Confirmation is needed. Run " + ChatColor.WHITE + builder + ChatColor.RED + " to confirm.");
                    },
                    commandSender -> {
                        commandSender.sendMessage(ChatColor.RED + "An error has occurred while running the command. Please try again.");
                    }
            );

            commandConfirmationManager.registerConfirmationProcessor(commandManager);

            Inject.bind(CommandManager.class, commandManager);
            Inject.bind(AnnotationParser.class, parser);
            Inject.bind(CommandConfirmationManager.class, commandConfirmationManager);
        } catch (Exception e) {
            System.out.println("Failed to initialize command manager");
            throw new RuntimeException(e);
        }
    }
}
