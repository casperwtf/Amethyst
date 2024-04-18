package wtf.casper.amethyst.paper.command;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.extra.confirmation.CommandConfirmationManager;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import wtf.casper.amethyst.core.inject.Inject;

import java.util.Collection;
import java.util.Optional;

public interface CloudCommand {
    default void registerCommands() {
        AnnotationParser<CommandSender> parser = Inject.get(AnnotationParser.class);
        CommandManager<CommandSender> commandManager = Inject.get(CommandManager.class);
        CommandConfirmationManager<CommandSender> commandConfirmationManager = Inject.get(CommandConfirmationManager.class);

        Collection<@NonNull Command<CommandSender>> parse = parser.parse(this);

        for (Command<CommandSender> command : parse) {
            Optional<Boolean> b = command.getCommandMeta().get(CommandConfirmationManager.META_CONFIRMATION_REQUIRED);
            if (b.isEmpty() || !b.get()) {
                continue;
            }

            Command.Builder<CommandSender> builder = null;

            for (CommandArgument<CommandSender, ?> argument : command.getArguments()) {
                if (argument.getOwningCommand() != null) {
                    break;
                }

                if (builder == null) {
                    builder = commandManager.commandBuilder(argument.getName())
                            .permission(command.getCommandPermission())
                            .handler(commandConfirmationManager.createConfirmationExecutionHandler());
                    continue;
                }

                builder = builder.literal(argument.getName());
            }

            builder = builder.literal("confirm");

            commandManager.command(builder.build());
        }
    }
}
