package wtf.casper.amethyst.paper.command;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.component.CommandComponent;
import org.incendo.cloud.processors.confirmation.ConfirmationManager;
import wtf.casper.amethyst.core.inject.Inject;
import wtf.casper.amethyst.paper.AmethystPlugin;

import java.util.Collection;

public interface CloudCommand {
    default void registerCommands(JavaPlugin plugin) {
        if (plugin == null) {
            throw new IllegalArgumentException("Plugin cannot be null");
        }

        AnnotationParser<CommandSender> parser;
        CommandManager<CommandSender> commandManager;
        ConfirmationManager<CommandSender> commandConfirmationManager;

        if (plugin instanceof AmethystPlugin amethystPlugin) {
            parser = Inject.get(AnnotationParser.class, amethystPlugin.getInjectionContainer());
            commandManager = Inject.get(CommandManager.class, amethystPlugin.getInjectionContainer());
            commandConfirmationManager = Inject.get(ConfirmationManager.class, amethystPlugin.getInjectionContainer());
        } else {
            parser = Inject.get(AnnotationParser.class);
            commandManager = Inject.get(CommandManager.class);
            commandConfirmationManager = Inject.get(ConfirmationManager.class);
        }

        Collection<@NonNull Command<CommandSender>> parse = parser.parse(this);

        for (Command<CommandSender> command : parse) {
            if (!command.commandMeta().contains(ConfirmationManager.META_CONFIRMATION_REQUIRED)) {
                continue;
            }
            boolean confirm = command.commandMeta().get(ConfirmationManager.META_CONFIRMATION_REQUIRED);
            if (!confirm) {
                continue;
            }

            Command.Builder<CommandSender> builder = null;

            for (CommandComponent<CommandSender> argument : command.nonFlagArguments()) {
                if (argument.type() != CommandComponent.ComponentType.LITERAL){
                    break;
                }

                if (builder == null) {
                    builder = commandManager.commandBuilder(argument.name())
                            .permission(command.commandPermission())
                            .handler(commandConfirmationManager.createExecutionHandler());
                    continue;
                }

                builder = builder.literal(argument.name());
            }

            builder = builder.literal("confirm");

            commandManager.command(builder.build());
        }
    }
}
