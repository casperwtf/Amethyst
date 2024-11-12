package wtf.casper.amethyst.paper.providers;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.bukkit.CloudBukkitCapabilities;
import org.incendo.cloud.component.CommandComponent;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.meta.SimpleCommandMeta;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import org.incendo.cloud.paper.PaperCommandManager;
import org.incendo.cloud.processors.cache.CaffeineCache;
import org.incendo.cloud.processors.confirmation.ConfirmationConfiguration;
import org.incendo.cloud.processors.confirmation.ConfirmationManager;
import org.incendo.cloud.processors.confirmation.ImmutableConfirmationConfiguration;
import wtf.casper.amethyst.core.inject.Inject;
import wtf.casper.amethyst.paper.AmethystPlugin;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

@Getter
@Setter
public class CloudCommandProvider {

    private PaperCommandManager<CommandSender> commandManager;

    public void setup(JavaPlugin plugin) {
        try {

            LegacyPaperCommandManager<CommandSender> commandManager = new LegacyPaperCommandManager<>(
                    plugin,
                    ExecutionCoordinator.asyncCoordinator(),
                    SenderMapper.identity()
            );

            //https://cloud.incendo.org/minecraft/paper/#brigadier
//            PaperCommandManager<CommandSourceStack> commandManager = PaperCommandManager.builder()
//                    .executionCoordinator(ExecutionCoordinator.asyncCoordinator())
//                    .buildOnEnable(plugin);

            if (commandManager.hasCapability(CloudBukkitCapabilities.BRIGADIER)) {
                plugin.getLogger().log(Level.INFO, "Enabling Brigadier support for " + plugin.getName());
                commandManager.registerLegacyPaperBrigadier();
            }

            if (commandManager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
                plugin.getLogger().log(Level.INFO, "Enabling asynchronous tab completion for " + plugin.getName());
                commandManager.registerAsynchronousCompletions();
            }

            AnnotationParser<CommandSender> parser = new AnnotationParser<>(commandManager, CommandSender.class,
                    parserParameters -> SimpleCommandMeta.empty()
            );

            ImmutableConfirmationConfiguration<CommandSender> build = ConfirmationConfiguration.<CommandSender>builder()
                    .cache(CaffeineCache.of(Caffeine.newBuilder().expireAfterWrite(30, TimeUnit.SECONDS).build()))
                    .noPendingCommandNotifier(sender -> sender.sendMessage(ChatColor.RED + "You have no pending command to confirm."))
                    .confirmationRequiredNotifier((sender, ctx) -> {
                        StringBuilder builder = new StringBuilder();

                        builder.append("/");
                        for (CommandComponent<CommandSender> nonFlagArgument : ctx.command().nonFlagArguments()) {
                            if (nonFlagArgument.type() != CommandComponent.ComponentType.LITERAL) {
                                builder.append(nonFlagArgument.name()).append(" ");
                            } else {
                                break;
                            }
                        }

                        builder.append("confirm");
                        sender.sendMessage(ChatColor.RED + "You must confirm this command by typing " + builder);
                    })
                    .build();

            ConfirmationManager<CommandSender> confirmationManager = ConfirmationManager.confirmationManager(build);
            commandManager.registerCommandPostProcessor(confirmationManager.createPostprocessor());

            if (plugin instanceof AmethystPlugin amethystPlugin) {
                Inject.bind(CommandManager.class, commandManager, amethystPlugin.getInjectionContainer());
                Inject.bind(AnnotationParser.class, parser, amethystPlugin.getInjectionContainer());
                Inject.bind(ConfirmationManager.class, confirmationManager, amethystPlugin.getInjectionContainer());
            } else {
                Inject.bind(CommandManager.class, commandManager);
                Inject.bind(AnnotationParser.class, parser);
                Inject.bind(ConfirmationManager.class, confirmationManager);
            }
        } catch (Exception e) {
            System.out.println("Failed to initialize command manager");
            throw new RuntimeException(e);
        }
    }
}
