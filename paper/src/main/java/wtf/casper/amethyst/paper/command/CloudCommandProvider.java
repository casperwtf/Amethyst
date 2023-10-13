package wtf.casper.amethyst.paper.command;

import cloud.commandframework.Command;
import cloud.commandframework.annotations.AnnotationParser;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import wtf.casper.amethyst.core.inject.Inject;

import java.util.Collection;

public interface CloudCommandProvider {
    default void registerCommands() {
        AnnotationParser<CommandSender> parser = Inject.get(AnnotationParser.class);
        Collection<? extends @NonNull Command<CommandSender>> parsed = parser.parse(this);
        System.out.println("Parsed: " + parsed.size() + " commands in " + getClass().getSimpleName() + ".");
    }
}
