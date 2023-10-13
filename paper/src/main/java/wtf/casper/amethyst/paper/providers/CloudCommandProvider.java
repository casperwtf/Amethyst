package wtf.casper.amethyst.paper.providers;

import cloud.commandframework.CloudCapability;
import cloud.commandframework.CommandManager;
import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator;
import cloud.commandframework.meta.SimpleCommandMeta;
import cloud.commandframework.paper.PaperCommandManager;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import wtf.casper.amethyst.core.inject.Inject;

import java.util.function.Function;

@Getter
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

//            CommandConfirmationManager<CommandSender> commandConfirmationManager = new CommandConfirmationManager<>(
//                    30,
//                    TimeUnit.SECONDS,
//                    ctx -> {
//                        ctx.getCommandContext().getSender().sendMessage(ChatColor.RED + "Confirmation is needed.");
//                    },
//                    commandSender -> {
//                        commandSender.sendMessage(ChatColor.RED + "An error has occurred.");
//                    }
//            );
//
//            commandConfirmationManager.registerConfirmationProcessor(commandManager);
//            commandManager.command(
//                    commandManager
//                            .commandBuilder("confirm")
//                            .handler(commandConfirmationManager.createConfirmationExecutionHandler())
//                            .build()
//            );

            Inject.bind(CommandManager.class, commandManager);
            Inject.bind(AnnotationParser.class, parser);
        } catch (Exception e) {
            System.out.println("Failed to initialize command manager");
            throw new RuntimeException(e);
        }
    }
}
