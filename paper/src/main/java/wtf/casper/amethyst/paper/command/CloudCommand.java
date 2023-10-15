package wtf.casper.amethyst.paper.command;

import cloud.commandframework.annotations.AnnotationParser;
import wtf.casper.amethyst.core.inject.Inject;

public interface CloudCommand {
    default void registerCommands() {
        Inject.get(AnnotationParser.class).parse(this);
    }
}
