package wtf.casper.amethyst.paper;

import cloud.commandframework.CommandManager;
import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator;
import cloud.commandframework.extra.confirmation.CommandConfirmationManager;
import cloud.commandframework.meta.SimpleCommandMeta;
import cloud.commandframework.paper.PaperCommandManager;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import wtf.casper.amethyst.core.inject.Inject;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Getter
public class CloudCommandHandler {

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
            commandManager.registerBrigadier();
            commandManager.registerAsynchronousCompletions();

            AnnotationParser<CommandSender> parser = new AnnotationParser<>(commandManager, CommandSender.class,
                    parserParameters -> SimpleCommandMeta.empty()
            );

            CommandConfirmationManager<CommandSender> commandConfirmationManager = new CommandConfirmationManager<>(
                    30,
                    TimeUnit.SECONDS,
                    ctx -> {
                        ctx.getCommandContext().getSender().sendMessage(ChatColor.RED + "Confirmation is needed.");
                    },
                    commandSender -> {
                        commandSender.sendMessage(ChatColor.RED + "An error has occurred.");
                    }
            );

            commandConfirmationManager.registerConfirmationProcessor(commandManager);
            commandManager.command(
                    commandManager
                            .commandBuilder("confirm")
                            .handler(commandConfirmationManager.createConfirmationExecutionHandler())
                            .build()
            );

            Inject.bind(CommandManager.class, commandManager);
            Inject.bind(AnnotationParser.class, parser);
        } catch (Exception e) {
            System.out.println("Failed to initialize command manager");
            throw new RuntimeException(e);
        }
    }
}
